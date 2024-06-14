package fr.stonehaven.discord.bot.command;

import fr.stonehaven.discord.bot.api.rest.mojang.profile.MojangProfileService;
import fr.stonehaven.discord.bot.api.rest.stonehaven.farmrun.player.FarmrunPlayerService;
import fr.stonehaven.discord.bot.api.rest.stonehaven.farmrun.player.skill.FarmrunSkillService;
import fr.stonehaven.discord.bot.exception.api.external.MojangProfileNotFoundException;
import fr.stonehaven.farmrun.item.service.infrastructure.api.rest.response.skill.FarmrunSkillResponse;
import fr.stonehaven.shfarmrunplayerservice.core.enums.FarmrunSkills;
import fr.stonehaven.shfarmrunplayerservice.core.exceptions.FarmrunPlayerNotFoundException;
import fr.stonehaven.shfarmrunplayerservice.core.exceptions.skill.SkillNotFoundException;
import fr.stonehaven.shfarmrunplayerservice.core.utils.SkillLevelUtils;
import fr.stonehaven.shfarmrunplayerservice.infrastructure.api.rest.response.player.FarmrunPlayerResponse;
import fr.stonehaven.shfarmrunplayerservice.infrastructure.api.rest.response.skill.SkillResponse;
import fr.stonehaven.shfarmrunplayerservice.infrastructure.api.rest.response.skill.player.PlayerSkillResponse;
import io.netty.handler.codec.string.LineSeparator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class SkillsCommand extends ListenerAdapter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final JDA jda;

    private static final String COMMAND_NAME = "skills";
    private static final String COMMAND_DESCRIPTION = "View a player's skills statistics";
    private static final String COMMAND_USER_ARGS_LABEL = "name";
    private static final String COMMAND_USER_ARGS_DESCRIPTION = "Minecraft username";

    private static final boolean isGuildOnly = false;

    private final MojangProfileService mojangProfileService;
    private final FarmrunPlayerService farmrunPlayerService;
    private final FarmrunSkillService farmrunSkillService;

    @PostConstruct
    public void post() {
        jda.upsertCommand(COMMAND_NAME, COMMAND_DESCRIPTION)
                .addOption(OptionType.STRING, COMMAND_USER_ARGS_LABEL, COMMAND_USER_ARGS_DESCRIPTION, true)
                .setGuildOnly(isGuildOnly)
                .queue();
        jda.addEventListener(this);
        logger.info("Registered {}", this.getClass().getSimpleName());
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getName().equals(COMMAND_NAME)) {

            event.deferReply().queue();

            final int embedColor = 0x4cff8a;
            final String minecraftUsername = Objects.requireNonNull(event.getOption(COMMAND_USER_ARGS_LABEL)).getAsString();
            final String thumbnailUrl = "https://visage.surgeplay.com/bust/" + minecraftUsername + ".png?y=-40";
            EmbedBuilder embedBuilder = new EmbedBuilder();
            try {
                long start = System.currentTimeMillis();
                UUID playerUuid = mojangProfileService.getPlayerUuid(minecraftUsername);
                System.out.println("Took " + (System.currentTimeMillis()-start) + "ms to fetch UUID");
                start = System.currentTimeMillis();
                FarmrunPlayerResponse farmrunPlayer = farmrunPlayerService.getFarmrunPlayer(playerUuid);
                System.out.println("Took " + (System.currentTimeMillis()-start) + "ms to fetch farmrunPlayer");
                Collection<PlayerSkillResponse> playerSkills = farmrunPlayer.getSkills();
                List<SkillResponse> skills = farmrunSkillService.getAll();
                embedBuilder = embedBuilder
                        .setColor(embedColor)
                        .setThumbnail(thumbnailUrl)
                        .setTitle("Skills de " + minecraftUsername, "https://stonehaven.fr")
                        .setDescription(
                                new StringBuilder("Skill le plus haut: `" + this.highestSkill(farmrunPlayer.getSkills(), skills)).append("`").append(System.lineSeparator()).append(System.lineSeparator())
                                        .append("Combat `" + this.getSkillLevel(FarmrunSkills.COMBAT, playerSkills) + "`/`" + SkillLevelUtils.MAX_SKILL_LEVEL).append("`").append(System.lineSeparator())
                                        .append("Minage `" + this.getSkillLevel(FarmrunSkills.MINING, playerSkills) + "`/`" + SkillLevelUtils.MAX_SKILL_LEVEL).append("`").append(System.lineSeparator())
                                        .append("Pêche `" + this.getSkillLevel(FarmrunSkills.FISHING, playerSkills) + "`/`" + SkillLevelUtils.MAX_SKILL_LEVEL).append("`").append(System.lineSeparator())
                                        .append("Agriculture `" + this.getSkillLevel(FarmrunSkills.FARMING, playerSkills) + "`/`" + SkillLevelUtils.MAX_SKILL_LEVEL).append("`").append(System.lineSeparator())
                                        .append("Bûcheron `" + this.getSkillLevel(FarmrunSkills.FORAGING, playerSkills) + "`/`" + SkillLevelUtils.MAX_SKILL_LEVEL).append("`").append(System.lineSeparator())
                            )
                        .setFooter("https://stonehaven.fr", event.getJDA().getSelfUser().getAvatarUrl());
            } catch (MojangProfileNotFoundException ex) {
                ex.printStackTrace();
            } catch (FarmrunPlayerNotFoundException ex) {
                ex.printStackTrace();
            } catch (SkillNotFoundException ex) {
                ex.printStackTrace();
            }

            event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
        }
    }

    private String highestSkill(Collection<PlayerSkillResponse> playerSkills, List<SkillResponse> skills) {
        PlayerSkillResponse skill = playerSkills.stream().max(Comparator.comparing(PlayerSkillResponse::getLevel)).get();
        return skills.stream().filter(s -> s.getId().equals(skill.getId())).findFirst().get().getName();
    }

    private int getSkillLevel(FarmrunSkills skill, Collection<PlayerSkillResponse> playerSkills) {
        return playerSkills.stream().filter(s -> s.getId().equals(skill.getId())).mapToInt(PlayerSkillResponse::getLevel).findAny().orElse(1);
    }
}
