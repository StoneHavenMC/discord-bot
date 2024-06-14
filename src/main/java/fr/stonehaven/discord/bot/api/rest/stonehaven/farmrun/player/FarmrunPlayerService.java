package fr.stonehaven.discord.bot.api.rest.stonehaven.farmrun.player;

import fr.stonehaven.shfarmrunplayerservice.core.exceptions.FarmrunPlayerNotFoundException;
import fr.stonehaven.shfarmrunplayerservice.infrastructure.api.rest.response.player.FarmrunPlayerResponse;
import fr.stonehaven.shfarmrunplayerservice.infrastructure.api.rest.response.player.TFarmrunPlayerResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class FarmrunPlayerService {

    @Value("${api.stonehaven.farmrun-player-service.base-url}")
    private String baseUrl;

    private static final String API_ERROR_MESSAGE = "Farmrun Player API is offline";

    private final RestTemplate template = new RestTemplate();

    @Cacheable(value = "getFarmrunPlayer", key = "#id")
    public FarmrunPlayerResponse getFarmrunPlayer(UUID id) throws FarmrunPlayerNotFoundException {
        TFarmrunPlayerResponse restResponse = template.getForObject(baseUrl + "/" + id, TFarmrunPlayerResponse.class);
        if (restResponse == null) throw new FarmrunPlayerNotFoundException(API_ERROR_MESSAGE);
        return restResponse.getPlayer();
    }
}
