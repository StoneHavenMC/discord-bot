package fr.stonehaven.discord.bot.api.rest.stonehaven.farmrun.item;

import fr.stonehaven.farmrun.item.service.core.exceptions.FarmrunItemTemplateNotFoundException;
import fr.stonehaven.farmrun.item.service.infrastructure.api.rest.response.FarmrunItemTemplateResponse;
import fr.stonehaven.farmrun.item.service.infrastructure.api.rest.response.FarmrunItemTemplatesResponse;
import fr.stonehaven.farmrun.item.service.infrastructure.api.rest.response.TFarmrunItemTemplateResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Service
public class FarmrunItemService {

    @Value("${api.stonehaven.farmrun-item-service.base-url}")
    private String baseUrl;

    private static final String API_ERROR_MESSAGE = "Farmrun Item Template API is offline";

    private final RestTemplate template = new RestTemplate();

    public FarmrunItemTemplateResponse getItemTemplate(UUID id) throws FarmrunItemTemplateNotFoundException {
        TFarmrunItemTemplateResponse restResponse = template.getForObject(baseUrl + "/" + id.toString(), TFarmrunItemTemplateResponse.class);
        if (restResponse == null) throw new FarmrunItemTemplateNotFoundException(API_ERROR_MESSAGE);
        return restResponse.getItem();
    }

    public List<FarmrunItemTemplateResponse> getAll() throws FarmrunItemTemplateNotFoundException {
        FarmrunItemTemplatesResponse restResponse = template.getForObject(baseUrl, FarmrunItemTemplatesResponse.class);
        if (restResponse == null) throw new FarmrunItemTemplateNotFoundException(API_ERROR_MESSAGE);
        return restResponse.getItems();
    }
}
