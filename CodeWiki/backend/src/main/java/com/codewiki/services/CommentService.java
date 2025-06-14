package com.codewiki.services;

import com.codewiki.models.Article;
import com.codewiki.models.Comment;
import com.codewiki.models.User;
import com.codewiki.repositories.ArticleRepository;
import com.codewiki.repositories.CommentRepository;
import com.codewiki.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepo;
    private final UserRepository userRepo;
    private final ArticleRepository articleRepo;

    public List<Comment> getCommentsByArticle(Long articleId) {
        return commentRepo.findByArticleId(articleId);
    }

    public Comment createComment(String content, Long articleId, Long authorId) {
        Article article = articleRepo.findById(articleId)
            .orElseThrow(() -> new RuntimeException("Article not found"));
        User author = userRepo.findById(authorId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setArticle(article);
        comment.setAuthor(author);
        return commentRepo.save(comment);
    }
}