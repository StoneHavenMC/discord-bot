package fr.stonehaven.discord.bot.api.rest.stonehaven.farmrun.player.collection;

import fr.stonehaven.shfarmrunplayerservice.core.exceptions.collection.CollectionNotFoundException;
import fr.stonehaven.shfarmrunplayerservice.infrastructure.api.rest.response.collection.category.CollectionCategoryResponse;
import fr.stonehaven.shfarmrunplayerservice.infrastructure.api.rest.response.collection.category.TCollectionCategoriesResponse;
import fr.stonehaven.shfarmrunplayerservice.infrastructure.api.rest.response.collection.category.TCollectionCategoryResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class FarmrunCollectionCategoryService {

    @Value("${api.stonehaven.farmrun-player-service.base-url}")
    private String baseUrl;

    private static final String API_ERROR_MESSAGE = "Farmrun Player API is offline";

    private final RestTemplate template = new RestTemplate();

    public CollectionCategoryResponse getCategory(String id) throws CollectionNotFoundException {
        TCollectionCategoryResponse restResponse = template.getForObject(baseUrl + "/collection/category/" + id, TCollectionCategoryResponse.class);
        if (restResponse == null) throw new CollectionNotFoundException(API_ERROR_MESSAGE);
        return restResponse.getCategory();
    }

    public List<CollectionCategoryResponse> getAll() throws CollectionNotFoundException {
        TCollectionCategoriesResponse restResponse = template.getForObject(baseUrl + "/collection/category", TCollectionCategoriesResponse.class);
        if (restResponse == null) throw new CollectionNotFoundException(API_ERROR_MESSAGE);
        return restResponse.getCategories().stream().toList();
    }
}
