package fr.stonehaven.discord.bot.exception;

public class RateLimitException extends Exception {

    public RateLimitException() {
    }

    public RateLimitException(String message) {
        super(message);
    }
}
