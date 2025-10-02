package me.bottdev.breezecore.modules.loaders;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.config.autoload.AutoLoadIndex;
import me.bottdev.breezeapi.di.BreezeContext;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.SimpleLogger;
import me.bottdev.breezeapi.modules.Module;
import me.bottdev.breezeapi.modules.ModuleClassLoader;
import me.bottdev.breezeapi.modules.ModuleLoader;
import me.bottdev.breezeapi.modules.ModulePreLoad;
import me.bottdev.breezeapi.di.index.ComponentIndex;
import me.bottdev.breezeapi.di.index.SupplierIndex;

import java.io.*;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@RequiredArgsConstructor
public class FolderModuleLoader implements ModuleLoader {

    @Getter
    private final ClassLoader parentClassLoader;
    @Getter
    private final BreezeContext context;
    @Getter
    private final Path targetDirectory;

    private final BreezeLogger logger = new SimpleLogger("FolderModuleLoader");

    private void createDirectoryIfNotExists() {
        File folder = this.targetDirectory.toFile();
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    @Override
    public List<ModulePreLoad> load() {
        createDirectoryIfNotExists();
        logger.info("Loading modules from directory {}", targetDirectory);

        List<ModulePreLoad> modules = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(targetDirectory, "*.jar")) {

            for (Path jar : stream) {
                processJar(jar).ifPresent(modules::add);
            }

        } catch (IOException ex) {
            throw new RuntimeException("Failed to scan modules directory", ex);
        }

        return modules;
    }

    private Optional<ModulePreLoad> processJar(Path jarPath) {
        try {

            URL jarUrl = jarPath.toUri().toURL();
            URLClassLoader loader = new ModuleClassLoader(new URL[]{jarUrl}, parentClassLoader);

            try (JarFile jarFile = new JarFile(jarPath.toFile())) {

                JarEntry entry = jarFile.getJarEntry("META-INF/breeze-modules-index.txt");
                if (entry == null) {
                    logger.warn("No breeze-modules-index.txt in {}", jarPath);
                    return Optional.empty();
                }

                try (InputStream in = jarFile.getInputStream(entry);
                     BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {

                    String line;
                    while ((line = reader.readLine()) != null) {
                        Optional<ModulePreLoad> preload = loadModuleFromLine(jarFile, loader, line);
                        if (preload.isPresent()) {
                            return preload;
                        }
                    }

                }
            }
        } catch (Exception ex) {
            logger.error("Failed to process jar " + jarPath, ex);
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private Optional<ModulePreLoad> loadModuleFromLine(JarFile jarFile, ClassLoader classLoader, String line) {
        String[] options = line.split(",");
        if (options.length < 3) {
            logger.warn("Invalid module line: {}", line);
            return Optional.empty();
        }

        String classPath = options[0];
        String name = options[1];
        String version = options[2];

        try {

            Class<? extends Module> clazz = (Class<? extends Module>) classLoader.loadClass(classPath);
            if (!Module.class.isAssignableFrom(clazz)) {
                logger.warn("{} does not implement Module.", clazz);
                return Optional.empty();
            }

            File dataFolder = createDataFolder(name);
            Constructor<?> constructor = clazz.getDeclaredConstructor(File.class);
            Supplier<Optional<Module>> moduleSupplier = () -> {
                try {
                    return Optional.of((Module) constructor.newInstance(dataFolder));
                } catch (Exception ex) {
                    logger.error("Failed to create module " + name + " from " + classPath, ex);
                }
                return Optional.empty();
            };

            AutoLoadIndex autoLoadIndex = readAutoLoadIndex(jarFile)
                    .orElseThrow(() -> new RuntimeException("Autoload configuration index is null"));
            SupplierIndex supplierIndex = readSupplierIndex(jarFile)
                    .orElseThrow(() -> new RuntimeException("Supplier index is null"));
            ComponentIndex componentIndex = readComponentIndex(jarFile)
                    .orElseThrow(() -> new RuntimeException("Component index is null"));

            logger.info("Loaded module {} {} from {}", name, version, classPath);

            return Optional.of(new ModulePreLoad(
                    classLoader,
                    autoLoadIndex,
                    supplierIndex,
                    componentIndex,
                    dataFolder.toPath(),
                    clazz,
                    moduleSupplier
            ));

        } catch (Exception ex) {
            logger.error("Failed to load module " + name + " " + version, ex);
        }
        return Optional.empty();
    }

    private File createDataFolder(String moduleName) {
        File dataFolder = targetDirectory.resolve(moduleName).toFile();
        if (!dataFolder.exists()) dataFolder.mkdir();
        return dataFolder;
    }

    private Optional<AutoLoadIndex> readAutoLoadIndex(JarFile jarFile) {
        try {

            JarEntry entry = jarFile.getJarEntry("META-INF/breeze-autoload-configuration-index.json");
            if (entry == null) {
                logger.warn("{} not found in {}", "META-INF/breeze-autoload-configuration-index.json", jarFile.getName());
                return Optional.of(new AutoLoadIndex());
            }

            try (InputStream in = jarFile.getInputStream(entry)) {
                String content = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                return AutoLoadIndex.fromJson(content);
            }

        } catch (Exception ex) {
            logger.error("Failed to read supplier index " + "META-INF/breeze-supplier-index.json" + " in " + jarFile.getName(), ex);
            return Optional.of(new AutoLoadIndex());
        }
    }


    private Optional<SupplierIndex> readSupplierIndex(JarFile jarFile) {
        try {

            JarEntry entry = jarFile.getJarEntry("META-INF/breeze-supplier-index.json");
            if (entry == null) {
                logger.warn("{} not found in {}", "META-INF/breeze-supplier-index.json", jarFile.getName());
                return Optional.of(new SupplierIndex());
            }

            try (InputStream in = jarFile.getInputStream(entry)) {
                String content = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                return SupplierIndex.fromJson(content);
            }

        } catch (Exception ex) {
            logger.error("Failed to read supplier index " + "META-INF/breeze-supplier-index.json" + " in " + jarFile.getName(), ex);
            return Optional.of(new SupplierIndex());
        }
    }

    private Optional<ComponentIndex> readComponentIndex(JarFile jarFile) {
        try {

            JarEntry entry = jarFile.getJarEntry("META-INF/breeze-component-index.json");
            if (entry == null) {
                logger.warn("{} not found in {}", "META-INF/breeze-component-index.json", jarFile.getName());
                return Optional.of(new ComponentIndex());
            }

            try (InputStream in = jarFile.getInputStream(entry)) {
                String content = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                return ComponentIndex.fromJson(content);
            }

        } catch (Exception ex) {
            logger.error("Failed to read supplier index " + "META-INF/breeze-component-index.json" + " in " + jarFile.getName(), ex);
            return Optional.of(new ComponentIndex());
        }
    }

}
