package me.bottdev.breezeapi.log;

import me.bottdev.breezeapi.log.colour.ColoredLogger;

import java.util.Deque;

public interface TreeLogger extends BreezeLogger, ColoredLogger {

    String SPACE_4 = "    ";
    String SPACE_3 = "   ";

    Deque<String> getStack();

    default String indent(String spacer) {
        if (getStack().isEmpty()) return "";
        StringBuilder indent = new StringBuilder();
        indent.append(spacer);
        if (getStack().size() > 1) {
            indent.append(SPACE_4.repeat(Math.max(0, getStack().size() - 2)));
        }
        return indent.toString();
    }

    default String formatTree(String spacer, String prefix, String message) {
        return applyColors(
                "[" +
                getName() +
                "] " +
                indent(spacer) +
                prefix +
                (getStack().isEmpty() || getStack().getLast().isEmpty() ? "" : "[" + getStack().getLast() + "]") +
                " " +
                message
        );
    }

    void logBranchTitle(String title);

    default void push(String title, String prefix) {
        logBranchTitle(formatTree(SPACE_4, "└─", title));
        getStack().push(prefix);
    }

    default void push(String title, String prefix, Object... args) {
        logBranchTitle(formatTree(SPACE_4, "└─", replaceArguments(title, args)));
        getStack().push(prefix);
    }

    default void pop() {
        if (!getStack().isEmpty()) getStack().pop();
    }

    default void withSection(String title, String prefix, Runnable task) {
        push(title, prefix);
        try {
            task.run();
        } finally {
            pop();
        }
    }

}
