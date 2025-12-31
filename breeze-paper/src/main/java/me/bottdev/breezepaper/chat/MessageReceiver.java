package me.bottdev.breezepaper.chat;

public interface MessageReceiver {

    default String replaceMessageArguments(String message, Object... args) {
        for (Object arg : args) {
            message = message.replaceFirst("\\{}", arg == null ? "null" : arg.toString());
        }
        return message;
    }

    default void sendMessage(String message, Object... args) {
        String formattedMessage = replaceMessageArguments(message, args);
        sendMessage(formattedMessage);
    }

    void sendMessage(String message);

}
