package fr.stonehaven.discord.bot.api.rest.stonehaven.farmrun.player.collection;

import fr.stonehaven.shfarmrunplayerservice.core.exceptions.collection.CollectionNotFoundException;
import fr.stonehaven.shfarmrunplayerservice.infrastructure.api.rest.response.collection.type.CollectionTypeResponse;
import fr.stonehaven.shfarmrunplayerservice.infrastructure.api.rest.response.collection.type.TCollectionTypeResponse;
import fr.stonehaven.shfarmrunplayerservice.infrastructure.api.rest.response.collection.type.TCollectionTypesResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class FarmrunCollectionTypeService {

    @Value("${api.stonehaven.farmrun-player-service.base-url}")
    private String baseUrl;

    private static final String API_ERROR_MESSAGE = "Farmrun Player API is offline";

    private final RestTemplate template = new RestTemplate();

    public CollectionTypeResponse getType(String id) throws CollectionNotFoundException {
        TCollectionTypeResponse restResponse = template.getForObject(baseUrl + "/collection/type/" + id, TCollectionTypeResponse.class);
        if (restResponse == null) throw new CollectionNotFoundException(API_ERROR_MESSAGE);
        return restResponse.getCollection();
    }

    public List<CollectionTypeResponse> getAll() throws CollectionNotFoundException {
        TCollectionTypesResponse restResponse = template.getForObject(baseUrl + "/collection/type", TCollectionTypesResponse.class);
        if (restResponse == null) throw new CollectionNotFoundException(API_ERROR_MESSAGE);
        return restResponse.getCollections().stream().toList();
    }
}
