package fr.stonehaven.discord.bot.service.gitbook;

import fr.stonehaven.discord.bot.dto.api.gitbook.search.APIGitbookAnswer;
import fr.stonehaven.discord.bot.dto.api.gitbook.search.APIGitbookQuestion;
import fr.stonehaven.discord.bot.dto.api.gitbook.search.GitbookAnswer;
import fr.stonehaven.discord.bot.exception.GitbookAPIException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GitbookService implements IGitbookService {

    @Value("${gitbook.api.url}")
    private String gitbookAPIURL;

    @Value("${gitbook.api.token}")
    private String gitbookAPIToken;

    private final RestTemplate restClient = new RestTemplate();

    public GitbookAnswer generateAnswer(String question) throws GitbookAPIException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(gitbookAPIToken);

        APIGitbookQuestion body = new APIGitbookQuestion();
        body.setQuery(question);

        HttpEntity<APIGitbookQuestion> request = new HttpEntity<>(body, headers);

        try {
            APIGitbookAnswer response = restClient.exchange(gitbookAPIURL + "/search/ask", HttpMethod.POST, request, APIGitbookAnswer.class).getBody();
            assert response != null;
            return response.getAnswer();
        } catch (Exception e) {
            e.printStackTrace();
            throw new GitbookAPIException(e.getMessage());
        }
    }
}
