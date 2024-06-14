package fr.stonehaven.discord.bot.exception.api.external;

public class MojangProfileNotFoundException extends Exception {

    public MojangProfileNotFoundException() {
    }

    public MojangProfileNotFoundException(String message) {
        super(message);
    }

    public MojangProfileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
