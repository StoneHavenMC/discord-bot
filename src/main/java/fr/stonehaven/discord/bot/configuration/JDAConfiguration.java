package fr.stonehaven.discord.bot.configuration;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JDAConfiguration {

    @Value("${discord.bot.token}")
    private String discordBotToken;

    @Value("${discord.bot.activity}")
    private String discordBotActivity;

    @Bean
    public JDABuilder getJDABuilder() {
        JDABuilder builder = JDABuilder.createDefault(discordBotToken);
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES);
        builder.setActivity(Activity.watching(discordBotActivity));
        return builder;
    }

    @Bean
    JDA getJDA(JDABuilder jdaBuilder) {
        return jdaBuilder.build();
    }
}
