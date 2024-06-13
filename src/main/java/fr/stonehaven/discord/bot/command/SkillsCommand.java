package fr.stonehaven.discord.bot.command;

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

import java.util.Objects;

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

            event.getHook().sendMessageEmbeds(new EmbedBuilder()
                    .setColor(embedColor)
                    .setThumbnail(thumbnailUrl)
                    .setTitle("Skills de " + minecraftUsername, "https://stonehaven.fr")
                    .setDescription("""
                            Skill le plus haut: `---`
                            
                            Combat `--`/`--`
                            Minage `--`/`--`
                            Pêche `--`/`--`
                            Agriculture `--`/`--`
                            Bucheron `--`/`--`
                            """)
                    .setFooter("https://stonehaven.fr", event.getJDA().getSelfUser().getAvatarUrl())
                    .build()
            ).queue();


        }
    }
}
