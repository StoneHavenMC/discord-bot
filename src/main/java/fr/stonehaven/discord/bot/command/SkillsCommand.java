package fr.stonehaven.discord.bot.command;

import fr.stonehaven.discord.bot.api.rest.mojang.profile.MojangProfileService;
import fr.stonehaven.discord.bot.api.rest.stonehaven.farmrun.player.FarmrunPlayerService;
import fr.stonehaven.discord.bot.api.rest.stonehaven.farmrun.player.skill.FarmrunSkillService;
import fr.stonehaven.discord.bot.exception.api.external.MojangProfileNotFoundException;
import fr.stonehaven.shfarmrunplayerservice.core.enums.FarmrunSkills;
import fr.stonehaven.shfarmrunplayerservice.core.exceptions.FarmrunPlayerNotFoundException;
import fr.stonehaven.shfarmrunplayerservice.core.exceptions.skill.SkillNotFoundException;
import fr.stonehaven.shfarmrunplayerservice.core.utils.SkillLevelUtils;
import fr.stonehaven.shfarmrunplayerservice.infrastructure.api.rest.response.player.FarmrunPlayerResponse;
import fr.stonehaven.shfarmrunplayerservice.infrastructure.api.rest.response.skill.player.PlayerSkillResponse;
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

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;

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
                UUID playerUuid = mojangProfileService.getPlayerUuid(minecraftUsername);
                FarmrunPlayerResponse farmrunPlayer = farmrunPlayerService.getFarmrunPlayer(playerUuid);
                embedBuilder = embedBuilder
                        .setColor(embedColor)
                        .setThumbnail(thumbnailUrl)
                        .setTitle("Skills de " + minecraftUsername, "https://stonehaven.fr")
                        .setDescription(this.getEmbedContent(farmrunPlayer))
                        .setFooter("https://stonehaven.fr", event.getJDA().getSelfUser().getAvatarUrl());
            } catch (MojangProfileNotFoundException ex) {
                ex.printStackTrace();
            } catch (FarmrunPlayerNotFoundException ex) {
                ex.printStackTrace();
            }

            event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
        }
    }

    private String highestSkill(Collection<PlayerSkillResponse> playerSkills) {
        try {
            PlayerSkillResponse skill = playerSkills.stream().max(Comparator.comparing(PlayerSkillResponse::getLevel)).get();
            return FarmrunSkills.getById(skill.getId()).getName();
        } catch (SkillNotFoundException ex) {
            return "?";
        }
    }

    private int getSkillLevel(FarmrunSkills skill, Collection<PlayerSkillResponse> playerSkills) {
        return playerSkills.stream().filter(s -> s.getId().equals(skill.getId())).mapToInt(PlayerSkillResponse::getLevel).findAny().orElse(1);
    }

    private String getEmbedContent(FarmrunPlayerResponse farmrunPlayer) {
        Collection<PlayerSkillResponse> playerSkills = farmrunPlayer.getSkills();
        StringBuilder builder = new StringBuilder()
                .append("Skill le plus haut: `").append(this.highestSkill(farmrunPlayer.getSkills())).append("`")
                .append(System.lineSeparator())
                .append(System.lineSeparator());
        for (FarmrunSkills skill : FarmrunSkills.values()) {
            builder = builder.append(skill.getName())
                    .append("`")
                    .append(this.getSkillLevel(skill, playerSkills))
                    .append("`/`")
                    .append(SkillLevelUtils.MAX_SKILL_LEVEL)
                    .append("`")
                    .append(System.lineSeparator());
        }
        return builder.toString();
    }
}
