package me.bottdev.breezeapi.di;

import me.bottdev.breezeapi.index.BreezeIndexSerializer;
import me.bottdev.breezeapi.index.types.BreezeComponentIndex;
import me.bottdev.breezeapi.index.types.BreezeSupplierIndex;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public interface ContextReader {

    BreezeContext getContext();

    BreezeIndexSerializer getSerializer();

    void read();

    default void read(ClassLoader classLoader) {
        readSuppliersFromClassLoader(classLoader);
        readComponentsFromClassLoader(classLoader);
    }

    default void readSuppliersFromClassLoader(ClassLoader classLoader) {
        String path = "META-INF/breeze-supplier-index.json";
        getContext().getLogger().info("Reading suppliers from {}", path);
        try (InputStream in = classLoader.getResourceAsStream(path)) {

            if (in == null) return;
            String content = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            Optional<BreezeSupplierIndex> optional = getSerializer().deserialize(content)
                    .map(index -> (BreezeSupplierIndex)index);

            optional.ifPresent(index -> readSuppliersFromIndex(index, classLoader));

        } catch (Exception ex) {
            getContext().getLogger().error("Failed to load suppliers from index", ex);
        }
    }

    void readSuppliersFromIndex(BreezeSupplierIndex index, ClassLoader classLoader);

    default void readComponentsFromClassLoader(ClassLoader classLoader) {
        String path = "META-INF/breeze-component-index.json";
        getContext().getLogger().info("Reading components from {}", path);
        try (InputStream in = classLoader.getResourceAsStream(path)) {

            if (in == null) return;
            String content = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            Optional<BreezeComponentIndex> optional = getSerializer().deserialize(content)
                    .map(index -> (BreezeComponentIndex)index);

            optional.ifPresent(index -> readComponentsFromIndex(index, classLoader));

        } catch (Exception ex) {
            getContext().getLogger().error("Failed to load components from index", ex);
        }
    }

    void readComponentsFromIndex(BreezeComponentIndex index, ClassLoader classLoader);

}
