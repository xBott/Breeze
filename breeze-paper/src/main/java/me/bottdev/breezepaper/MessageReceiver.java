package me.bottdev.breezepaper;

public interface MessageReceiver {

    default void sendMessage(String message, Object... args) {
        for (Object arg : args) {
            message = message.replaceFirst("\\{}", arg == null ? "null" : arg.toString());
        }
        sendMessage(message);
    }

    void sendMessage(String message);

}
