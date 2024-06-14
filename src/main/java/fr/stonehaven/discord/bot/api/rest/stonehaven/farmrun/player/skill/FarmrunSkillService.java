package fr.stonehaven.discord.bot.api.rest.stonehaven.farmrun.player.skill;

import fr.stonehaven.shfarmrunplayerservice.core.exceptions.skill.SkillNotFoundException;
import fr.stonehaven.shfarmrunplayerservice.infrastructure.api.rest.response.skill.SkillResponse;
import fr.stonehaven.shfarmrunplayerservice.infrastructure.api.rest.response.skill.TSkillResponse;
import fr.stonehaven.shfarmrunplayerservice.infrastructure.api.rest.response.skill.TSkillsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class FarmrunSkillService {

    @Value("${api.stonehaven.farmrun-player-service.base-url}")
    private String baseUrl;

    private static final String API_ERROR_MESSAGE = "Farmrun Player API is offline";

    private final RestTemplate template = new RestTemplate();

    public SkillResponse getSkill(String id) throws SkillNotFoundException {
        TSkillResponse restResponse = template.getForObject(baseUrl + "/skill/" + id, TSkillResponse.class);
        if (restResponse == null) throw new SkillNotFoundException(API_ERROR_MESSAGE);
        return restResponse.getSkill();
    }

    public List<SkillResponse> getAll() throws SkillNotFoundException {
        TSkillsResponse restResponse = template.getForObject(baseUrl + "/skill", TSkillsResponse.class);
        if (restResponse == null) throw new SkillNotFoundException(API_ERROR_MESSAGE);
        return restResponse.getSkills().stream().toList();
    }
}
