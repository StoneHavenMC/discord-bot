package fr.stonehaven.discord.bot.service.gitbook;

import fr.stonehaven.discord.bot.dto.api.gitbook.search.GitbookAnswer;
import fr.stonehaven.discord.bot.exception.GitbookAPIException;

public interface IGitbookService {

    GitbookAnswer generateAnswer(String question) throws GitbookAPIException;
}
