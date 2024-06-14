package fr.stonehaven.discord.bot.api.rest.mojang.profile;

import fr.stonehaven.discord.bot.api.rest.mojang.profile.dto.MojangProfile;
import fr.stonehaven.discord.bot.exception.api.external.MojangProfileNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class MojangProfileService {

    @Value("${api.mojang.base-url}")
    private String baseUrl;

    private static final String API_ERROR_MESSAGE = "Mojang API is offline";

    private final RestTemplate template = new RestTemplate();

    @Cacheable(value = "getPlayerUuid", key = "#playerName")
    public UUID getPlayerUuid(String playerName) throws MojangProfileNotFoundException {
        MojangProfile restResponse = template.getForObject(baseUrl + "/users/profiles/minecraft/" + playerName, MojangProfile.class);
        if (restResponse == null) throw new MojangProfileNotFoundException(API_ERROR_MESSAGE);
        return fromString(restResponse.id());
    }

    public static UUID fromString(String input) {
        return UUID.fromString(input.replaceFirst(
                "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
    }
}
