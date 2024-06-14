package fr.stonehaven.discord.bot.command;

import fr.stonehaven.discord.bot.api.rest.mojang.profile.MojangProfileService;
import fr.stonehaven.discord.bot.api.rest.stonehaven.farmrun.player.FarmrunPlayerService;
import fr.stonehaven.discord.bot.exception.api.external.MojangProfileNotFoundException;
import fr.stonehaven.shfarmrunplayerservice.core.enums.collections.CollectionCategories;
import fr.stonehaven.shfarmrunplayerservice.core.enums.collections.CollectionTypes;
import fr.stonehaven.shfarmrunplayerservice.core.exceptions.FarmrunPlayerNotFoundException;
import fr.stonehaven.shfarmrunplayerservice.core.exceptions.collection.CollectionNotFoundException;
import fr.stonehaven.shfarmrunplayerservice.infrastructure.api.rest.response.collection.player.PlayerCollectionResponse;
import fr.stonehaven.shfarmrunplayerservice.infrastructure.api.rest.response.player.FarmrunPlayerResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

//TODO Change SelectMenu options methode (remove hard codded values..)
//TODO Change all String by LocalizationFunction in the future.

@Component
@RequiredArgsConstructor
public class CollectionsCommand extends ListenerAdapter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final JDA jda;

    private static final String COMMAND_NAME = "collections";
    private static final String COMMAND_DESCRIPTION = "View a player's collections statistics";
    private static final String COMMAND_USER_ARGS_LABEL = "name";
    private static final String COMMAND_USER_ARGS_DESCRIPTION = "Minecraft username";

    private static final boolean isGuildOnly = false;
    private final MojangProfileService mojangProfileService;
    private final FarmrunPlayerService farmrunPlayerService;

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

            User user = event.getUser();

            final int embedColor = 0x4cff8a;
            final String minecraftUsername = Objects.requireNonNull(event.getOption(COMMAND_USER_ARGS_LABEL)).getAsString();
            final String thumbnailUrl = "https://visage.surgeplay.com/bust/" + minecraftUsername + ".png?y=-40";
            EmbedBuilder embedBuilder = new EmbedBuilder();

            try {
                UUID playerUuid = mojangProfileService.getPlayerUuid(minecraftUsername);
                FarmrunPlayerResponse farmrunPlayer = farmrunPlayerService.getFarmrunPlayer(playerUuid);
                embedBuilder = embedBuilder.setColor(embedColor)
                        .setThumbnail(thumbnailUrl)
                        .setTitle("Collections de " + minecraftUsername, "https://stonehaven.fr")
                        .setDescription(this.getCategoryEmbedContent(farmrunPlayer))
                        .setFooter("https://stonehaven.fr", event.getJDA().getSelfUser().getAvatarUrl()
                        );
            } catch (MojangProfileNotFoundException ex) {
                ex.printStackTrace();
            } catch (FarmrunPlayerNotFoundException ex) {
                ex.printStackTrace();
            }

            StringSelectMenu.Builder menuBuilder = StringSelectMenu.create("collections_" + user.getId() + "_select_menu");
            for (CollectionCategories category : CollectionCategories.values()) {
                menuBuilder.addOption(category.getName(), category.getId());
            }
            menuBuilder.setPlaceholder("Consulter une catégorie spécifique");

            event.getHook().sendMessageEmbeds(embedBuilder.build()).addActionRow(menuBuilder.build()).queue();
        }
    }

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent e) {
        User user = e.getUser();
        if (!e.getSelectMenu().getId().equals("collections_" + user.getId() + "_select_menu")) return;
        e.deferReply().queue();

        try {
            CollectionCategories category = CollectionCategories.getById(e.getValues().get(0));

            final int embedColor = 0x4cff8a;
            final String minecraftUsername = "SummerAPI";
            final String thumbnailUrl = "https://visage.surgeplay.com/bust/" + minecraftUsername + ".png?y=-40";
            EmbedBuilder embedBuilder = new EmbedBuilder();

            UUID playerUuid = mojangProfileService.getPlayerUuid(minecraftUsername);
            FarmrunPlayerResponse farmrunPlayer = farmrunPlayerService.getFarmrunPlayer(playerUuid);
            embedBuilder = embedBuilder.setColor(embedColor)
                    .setThumbnail(thumbnailUrl)
                    .setTitle("Collections de " + minecraftUsername, "https://stonehaven.fr")
                    .setDescription(this.getTypeEmbedContent(category, farmrunPlayer))
                    .setFooter("https://stonehaven.fr", e.getJDA().getSelfUser().getAvatarUrl()
                    );

            e.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
        } catch (CollectionNotFoundException ex) {
            ex.printStackTrace();
        } catch (MojangProfileNotFoundException ex) {
            ex.printStackTrace();
        } catch (FarmrunPlayerNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private String getCategoryEmbedContent(FarmrunPlayerResponse farmrunPlayer) {
        StringBuilder builder = new StringBuilder()
                .append("Collections terminées: `").append(this.getCompleteCollections(farmrunPlayer, null)).append("`/`").append(CollectionTypes.values().length).append("`")
                .append(System.lineSeparator())
                .append(System.lineSeparator());
        for (CollectionCategories collectionCategory : CollectionCategories.values()) {
            builder = builder.append(collectionCategory.getName())
                    .append("`")
                    .append(this.getCategoryCompleteSteps(collectionCategory, farmrunPlayer))
                    .append("`/`")
                    .append(this.getCategoryTotalSteps(collectionCategory))
                    .append("`")
                    .append(System.lineSeparator());
        }
        return builder.toString();
    }

    private String getTypeEmbedContent(CollectionCategories category, FarmrunPlayerResponse farmrunPlayer) {
        StringBuilder builder = new StringBuilder()
                .append("Collections terminées: `").append(this.getCompleteCollections(farmrunPlayer, category)).append("`/`").append(this.getCollections(category)).append("`")
                .append(System.lineSeparator())
                .append(System.lineSeparator());
        for (CollectionTypes collectionType : CollectionTypes.values()) {
            if (collectionType.getCategory() != category) continue;
            builder = builder.append(collectionType.getName())
                    .append("`")
                    .append(this.getCollectionLevel(collectionType, farmrunPlayer))
                    .append("`/`")
                    .append(collectionType.getSteps().size())
                    .append("`")
                    .append(System.lineSeparator());
        }
        return builder.toString();
    }

    private int getCompleteCollections(FarmrunPlayerResponse farmrunPlayer, CollectionCategories category) {
        int count = 0;
        for (CollectionTypes collectionType : CollectionTypes.values()) {
            if (category != null && collectionType.getCategory() != category) continue;
            if (this.getCollectionLevel(collectionType, farmrunPlayer) != collectionType.getSteps().size()) continue;
            count++;
        }
        return count;
    }

    private int getCollectionLevel(CollectionTypes collectionType, FarmrunPlayerResponse farmrunPlayer) {
        return farmrunPlayer.getCollections().stream().filter(c -> c.getId().equals(collectionType.getId())).mapToInt(PlayerCollectionResponse::getLevel).findFirst().orElse(0);
    }

    private int getCategoryCompleteSteps(CollectionCategories category, FarmrunPlayerResponse farmrunPlayer) {
        int count = 0;
        for (CollectionTypes type : CollectionTypes.values()) {
            if (type.getCategory() != category) continue;

            count += farmrunPlayer.getCollections().stream().filter(c -> c.getId().equals(type.getId())).mapToInt(PlayerCollectionResponse::getLevel).findFirst().orElse(0);
        }
        return count;
    }

    private int getCollections(CollectionCategories category) {
        return (int) Arrays.stream(CollectionTypes.values()).filter(c -> c.getCategory() == category).count();
    }

    private int getCategoryTotalSteps(CollectionCategories category) {
        int count = 0;
        for (CollectionTypes type : CollectionTypes.values()) {
            if (type.getCategory() != category) continue;

            count += type.getSteps().size();
        }
        return count;
    }
}
