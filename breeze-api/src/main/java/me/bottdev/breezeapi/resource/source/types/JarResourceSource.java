package me.bottdev.breezeapi.resource.source.types;

import me.bottdev.breezeapi.commons.file.input.BreezeStreamReader;
import me.bottdev.breezeapi.commons.file.output.BreezeFileWriter;
import me.bottdev.breezeapi.commons.file.temp.TempFile;
import me.bottdev.breezeapi.commons.file.temp.TempFiles;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.platforms.SL4JLogPlatform;
import me.bottdev.breezeapi.resource.ResourceTree;
import me.bottdev.breezeapi.resource.annotations.sources.JarSource;
import me.bottdev.breezeapi.resource.source.ResourceSource;
import me.bottdev.breezeapi.resource.source.SourceType;
import me.bottdev.breezeapi.resource.types.FileResource;
import me.bottdev.breezeapi.resource.types.file.SingleFileResource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarResourceSource implements ResourceSource {

    private final BreezeLogger logger = SL4JLogPlatform.getFactory().simple("JarSource");

    @Override
    public ResourceTree<FileResource> provide(Method method) {

        ResourceTree<FileResource> resourceTree = new ResourceTree<>();

        if (!method.isAnnotationPresent(JarSource.class)) return resourceTree;
        JarSource annotation = method.getAnnotation(JarSource.class);
        String pathString = (annotation.path());

        URL url = getClass().getClassLoader().getResource(pathString);

        if (url == null) {
            logger.warn("Could not find resource {} in jar resources.", pathString);
            return resourceTree;
        }

        if (!url.getProtocol().equals("file")) {
            logger.warn("Resource {} is not file.", pathString);
            return resourceTree;
        }

        Path path = Paths.get(url.getPath());
        if (Files.isDirectory(path)) {

            Map<String, FileResource> resources = createTreeFileResources(pathString);
            resourceTree.addAll(resources);

        } else {

            createSingleFileResource(pathString).ifPresent(resource ->
                    resourceTree.add("", resource)
            );

        }

        return resourceTree;
    }

    private Optional<FileResource> createSingleFileResource(String path) {

        Optional<TempFile> targetOptional = TempFiles.create(Path.of(path));
        if (targetOptional.isEmpty()) return Optional.empty();

        TempFile target = targetOptional.get();

        try {

            InputStream sourceStream = getClass().getClassLoader().getResourceAsStream(path);

            if (sourceStream == null) {
                throw new FileNotFoundException(path.toString());
            }

            BreezeFileWriter.INSTANCE.writeChunks(target.toFile(), out ->
                    BreezeStreamReader.INSTANCE.readChunks(sourceStream, (data, length) ->
                            out.write(data, 0, length)
                    )
            );

            SingleFileResource resource = new SingleFileResource(target, SourceType.DRIVE);
            return Optional.of(resource);

        } catch (IOException ex) {
            TempFiles.delete(target);
            logger.error("Could not write temp file", ex);
        }

        return Optional.empty();
    }


    private Map<String, FileResource> createTreeFileResources(String resourcePath) {

        HashMap<String, FileResource> resources = new HashMap<>();

        try {

            Enumeration<URL> urls = getClass().getClassLoader().getResources(resourcePath);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                String protocol = url.getProtocol();

                if ("file".equals(protocol)) {
                    Map<String, FileResource> read = readResourcesFromFileSystem(resourcePath);
                    resources.putAll(read);

                } else if ("jar".equals(protocol)) {
                    Map<String, FileResource> read = readResourcesFromJar(resourcePath);
                    resources.putAll(read);
                }
            }

        } catch (Exception ex) {
            logger.error("Could not load resources.", ex);
            resources.values().forEach(resource -> TempFiles.delete(resource.getTempFile()));

        }

        return resources;
    }

    public Map<String, FileResource> readResourcesFromFileSystem(String resourcePath) throws Exception {

        HashMap<String, FileResource> resources = new HashMap<>();
        Enumeration<URL> urls = getClass().getClassLoader().getResources(resourcePath);

        while (urls.hasMoreElements()) {

            URL url = urls.nextElement();
            if (!"file".equals(url.getProtocol())) continue;

            Path dir = Path.of(url.toURI());
            Files.walk(dir)
                    .filter(Files::isRegularFile)
                    .forEach(path -> {

                        Path relativePath = dir.relativize(path);
                        String pathString = resourcePath + File.separator + relativePath;

                        Optional<FileResource> resourceOptional = createSingleFileResource(pathString);
                        resourceOptional.ifPresent(resource ->
                                resources.put(pathString, resource)
                        );

                    });
        }

        return resources;
    }

    public Map<String, FileResource> readResourcesFromJar(String resourcePath) throws IOException {

        HashMap<String, FileResource> resources = new HashMap<>();
        Enumeration<URL> urls = getClass().getClassLoader().getResources(resourcePath);

        while (urls.hasMoreElements()) {

            URL url = urls.nextElement();
            if (!"jar".equals(url.getProtocol())) continue;

            String jarPath = url.getPath().substring(5, url.getPath().indexOf("!"));
            jarPath = URLDecoder.decode(jarPath, StandardCharsets.UTF_8);

            try (JarFile jar = new JarFile(jarPath)) {

                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();

                    if (!entry.isDirectory() && entry.getName().startsWith(resourcePath + "/")) {

                        String relativePath = entry.getName().substring(resourcePath.length() + 1);

                        Optional<FileResource> resourceOptional = createSingleFileResource(relativePath);
                        resourceOptional.ifPresent(resource ->
                                resources.put(relativePath, resource)
                        );

                    }
                }
            }
        }

        return resources;
    }


}
