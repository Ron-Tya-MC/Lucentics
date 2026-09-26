package io.github.rontyamc.lucentics.common.dict;

import io.github.rontyamc.lucentics.Lucentics;

public enum Warns{
    WARN_MESSAGE_TOO_MANY_ARGS("Too many arguments in the following warning message:", "[Lucentics: WarnSystem]"),
    WARN_MESSAGE_INSUFFICIENT_ARGS("Not enough arguments in the following warning message:", "[Lucentics: WarnSystem]"),
    UNPROCESSED_LEFTOVER_BEFORE_COMMIT("Context committed with apparent unprocessed leftover: (Caller: {}, Leftover:{}) " +
            "Note that this leftover will be lost. If you handled it elsewhere, call markProcessed() to acknowledge it."),
    UNEXPECTED_LEFTOVER_AFTER_COMMIT("Unexpected leftover after commit: (Leftover: {}, Dimension: {}, Position: {}, BlockId: {})"),
    FAILED_TO_RESOLVE_ID("Failed to resolve ID path; falling back to \"unknown\". You MUST include a non-empty item/fluid output " +
            "or specify a path using path(String path) to prevent recipe collisions.", "[Lucentics: DataGen]");

    private final String message;
    private final int argCount;

    Warns(String message) {
        this(message, "[Lucentics]");
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
