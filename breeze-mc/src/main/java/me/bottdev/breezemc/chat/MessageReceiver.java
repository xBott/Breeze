package me.bottdev.breezemc.chat;

import java.util.regex.Matcher;

public interface MessageReceiver {

    default String replaceMessageArguments(String message, Object... args) {
        if (message == null) return "";

        for (Object arg : args) {
            String replacement;
            if (arg == null) {
                replacement = "null";
            } else {
                replacement = Matcher.quoteReplacement(String.valueOf(arg));
            }
            message = message.replaceFirst("\\{\\}", replacement);
        }
        return message;
    }



    default void sendMessage(String message, Object... args) {
        String formattedMessage = replaceMessageArguments(message, args);
        sendMessage(formattedMessage);
    }

    void sendMessage(String message);

}
