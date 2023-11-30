package fr.stonehaven.discord.bot.command;

import fr.stonehaven.discord.bot.dto.api.gitbook.search.GitbookAnswer;
import fr.stonehaven.discord.bot.service.gitbook.IGitbookService;
import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
public class AskWikiCommand extends ACooldownCommand {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final JDA jda;
    private final IGitbookService gitbookService;

    public AskWikiCommand(JDA jda, IGitbookService gitbookService) {
        super(5_000); // 5 seconds
        this.jda = jda;
        this.gitbookService = gitbookService;
    }

    @PostConstruct
    public void post() {
        jda.updateCommands().addCommands(
                Commands.slash("ask", "Poser une question au Wiki StoneHaven")
                        .addOption(OptionType.STRING, "question", "Votre question", true, false)
        ).queue();
        jda.addEventListener(this);
        logger.info("Registered " + this.getClass().getSimpleName());
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if (!e.getName().equalsIgnoreCase("ask")) return;
        e.deferReply().queue();
        if (e.getOption("question") == null) return;
        String question = e.getOption("question").getAsString();

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("**" + question + "**");

        if(!canUse(e.getUser().getId())) {
            builder.setColor(Color.RED);
            builder.setDescription("Veuillez patienter quelques secondes avant de pouvoir ré-effectuer cette commande.");
            e.getHook().sendMessageEmbeds(builder.build()).queue();
            return;
        }

        try {
            GitbookAnswer response = gitbookService.generateAnswer(question);

            builder.setColor(Color.PINK);
            builder.setDescription(response.getText()
                    .replace("[", "*")
                    .replace("]", "*")
            );

            /*AtomicInteger index = new AtomicInteger(1);
            response.getFollowupQuestions().stream().limit(3).forEach(suggestion -> {
                builder.addField("Suggestion #" + index.get() , suggestion, false);
                index.getAndIncrement();
            });*/
        } catch (Exception ex) {
            ex.printStackTrace();
            builder.setColor(Color.RED);
            builder.setDescription("Une erreur est survenue lors de la génération de votre réponse, veuillez ré-essayer une autre question.");
        }
        storeUse(e.getUser().getId());
        e.getHook().sendMessageEmbeds(builder.build()).queue();
    }

}
