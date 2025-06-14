package com.codewiki.services;

import com.codewiki.models.Article;
import com.codewiki.models.User;
import com.codewiki.models.Vote;
import com.codewiki.repositories.ArticleRepository;
import com.codewiki.repositories.UserRepository;
import com.codewiki.repositories.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepo;
    private final UserRepository userRepo;
    private final ArticleRepository articleRepo;

    @Transactional
    public void vote(Long articleId, Long userId, boolean isUpvote) {
        Article article = articleRepo.findById(articleId)
            .orElseThrow(() -> new RuntimeException("Article not found"));
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Удаляем предыдущий голос, если он есть
        voteRepo.deleteByUserIdAndArticleId(userId, articleId);

        Vote vote = new Vote();
        vote.setArticle(article);
        vote.setUser(user);
        vote.setUpvote(isUpvote);
        voteRepo.save(vote);

        // Обновляем репутацию автора статьи
        int voteImpact = isUpvote ? 1 : -1;
        User author = article.getAuthor();
        author.setReputation(author.getReputation() + voteImpact);
        userRepo.save(author);
    }
}