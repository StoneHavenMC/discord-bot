package fr.stonehaven.discord.bot.command;

import fr.stonehaven.discord.bot.dto.api.gitbook.search.GitbookAnswer;
import fr.stonehaven.discord.bot.service.gitbook.IGitbookService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
@RequiredArgsConstructor
public class PingCommand extends ListenerAdapter {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final JDA jda;

    @PostConstruct
    public void post() {
        jda.upsertCommand(
                Commands.slash("ping", "Calculer le temps de réponse du bot")
        ).queue();
        jda.addEventListener(this);
        logger.info("Registered " + this.getClass().getSimpleName());
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if (!e.getName().equalsIgnoreCase("ping")) return;
        e.deferReply().queue();

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("**Ping**");
            builder.setColor(Color.GRAY);
            long latency = e.getJDA().getGatewayPing();
            builder.setDescription("Le ping du bot est actuellement de " + latency + "ms");
        e.getHook().sendMessageEmbeds(builder.build()).queue();
    }

}
