package me.bottdev.breezeapi.command;

import kotlin.jvm.functions.Function0;

import java.util.Map;
import java.util.Optional;

public interface CommandNode {

    String getValue();

    String getDisplayName();

    Map<String, CommandNode> getChildren();

    default boolean hasChildren() {
        return !getChildren().isEmpty();
    }

    default void addChild(CommandNode child) {
        getChildren().put(child.getValue(), child);
    }

    default Optional<CommandNode> getChild(String value) {
        return Optional.ofNullable(getChildren().get(value));
    }

    default CommandNode getOrCreateChild(String value, Function0<CommandNode> supplier) {

        return getChild(value).orElseGet(() -> {

            CommandNode child = supplier.invoke();
            addChild(child);
            return child;

        });

    }

}
