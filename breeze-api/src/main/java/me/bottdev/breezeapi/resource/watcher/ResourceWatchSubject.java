package me.bottdev.breezeapi.resource.watcher;

import java.nio.file.Path;
import java.util.Optional;

public interface ResourceWatchSubject<T> {

    @FunctionalInterface
    interface Registration {
        void accept(Path path, String key);
    }

    T getTarget();

    String getEventId();

    Optional<Path> getPath();

    Optional<String> getRegistrationKey();

    default void ifPresent(Registration registration) {
        getPath().ifPresent(path ->
                getRegistrationKey().ifPresent(registrationKey ->
                        registration.accept(path, registrationKey)
                )
        );
    }

}
