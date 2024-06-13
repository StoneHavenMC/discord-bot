package fr.stonehaven.discord.bot.command;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

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

            event.getHook().sendMessageEmbeds(new EmbedBuilder()
                    .setColor(embedColor)
                    .setThumbnail(thumbnailUrl)
                    .setTitle("Skills de " + minecraftUsername, "https://stonehaven.fr")
                    .setDescription("""
                            Collections terminées: `--`/`--`
                            
                            Combat `--`/`--`
                            Minage `--`/`--`
                            Pêche `--`/`--`
                            Agriculture `--`/`--`
                            Bucheron `--`/`--`
                            """)
                    .setFooter("https://stonehaven.fr", event.getJDA().getSelfUser().getAvatarUrl())
                    .build()
            ).addActionRow(
                    StringSelectMenu.create("collections_" + user.getId() + "_select_menu")
                            .addOptions(
                                    SelectOption.of("Combat", "1"),
                                    SelectOption.of("Minage", "2"),
                                    SelectOption.of("Pêche", "3"),
                                    SelectOption.of("Agriculture", "4"),
                                    SelectOption.of("Bucheron", "5"))
                            .setPlaceholder("Consulter une catégorie spécifique")
                            .build()
            ).queue();

        }
    }
}
