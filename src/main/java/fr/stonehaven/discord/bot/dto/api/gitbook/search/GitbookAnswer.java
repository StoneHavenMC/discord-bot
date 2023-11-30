package fr.stonehaven.discord.bot.dto.api.gitbook.search;

import lombok.Getter;

import java.util.List;

public class GitbookAnswer {

    @Getter
    private String text;

    @Getter
    private List<String> followupQuestions;
}
