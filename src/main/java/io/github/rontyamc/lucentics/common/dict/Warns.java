package io.github.rontyamc.lucentics.common.dict;

import io.github.rontyamc.lucentics.Lucentics;

public enum Warns{
    WARN_MESSAGE_TOO_MANY_ARGS("Following warn message has too many arguments"),
    WARN_MESSAGE_INSUFFICIENT_ARGS("Following warn message's arguments are insufficient"),
    UNEXPECTED_LEFTOVER_AFTER_COMMIT("Unexpected leftover after commit: (Leftover: {}, Dimension: {}, Position: {}, BlockId: {})"),
    FAILED_TO_RESOLVE_ID("Failed to resolve ID path; falling back to \"unknown\". You MUST include a non-empty item/fluid output " +
            "or specify a path using path(String path) to prevent recipe collisions.", "[Lucentics: DataGen]");

    private final String message;
    private final int argCount;

    Warns(String message) {
        this.message = "[Lucentics] " + message;
        this.argCount = (message.length() - message.replace("{}", "").length()) / 2;
    }
    Warns(String message, String prefix) {
        this.message = prefix + " " + message;
        this.argCount = (message.length() - message.replace("{}", "").length()) / 2;
    }

    public void cast(Object... args) {
        if (args.length > this.argCount) {
            WARN_MESSAGE_TOO_MANY_ARGS.cast();
        }
        else if (args.length < this.argCount) {
            WARN_MESSAGE_INSUFFICIENT_ARGS.cast();
        }
        Lucentics.LOGGER.warn(message, args);
    }
}
