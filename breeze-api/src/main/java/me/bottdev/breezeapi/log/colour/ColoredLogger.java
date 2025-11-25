package me.bottdev.breezeapi.log.colour;

public interface ColoredLogger {

    default String applyColors(String message) {

        String result = message;

        for (AnsiColor color : AnsiColor.values()) {
            String target = String.format("<%s>", color.name().toLowerCase());
            result = result.replace(target, color.code());
        }

        return result;

    }

}
