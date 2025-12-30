package me.bottdev.breezeapi.resource.watcher;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.events.EventBus;
import me.bottdev.breezeapi.lifecycle.LifecycleBuilder;
import me.bottdev.breezeapi.resource.watcher.types.SingleResourceWatcher;
import me.bottdev.breezeapi.resource.watcher.types.TreeResourceWatcher;

import java.io.IOException;

public interface ResourceWatcherBuilder<T extends AbstractResourceWatcher<?>> extends LifecycleBuilder<T> {

    @Getter
    @RequiredArgsConstructor
    class Single implements ResourceWatcherBuilder<SingleResourceWatcher> {

        private final EventBus eventBus;

        @Override
        public SingleResourceWatcher build() {
            try {
                return new SingleResourceWatcher(eventBus);
            } catch (IOException ex) {
                return null;
            }
        }
    }

    @Getter
    @RequiredArgsConstructor
    class Tree implements ResourceWatcherBuilder<TreeResourceWatcher> {

        private final EventBus eventBus;

        @Override
        public TreeResourceWatcher build() {
            try {
                return new TreeResourceWatcher(eventBus);
            } catch (IOException ex) {
                return null;
            }
        }
    }

}
