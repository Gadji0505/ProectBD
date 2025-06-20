package com.codewiki.codewiki.repository;
import java.util.List;
import com.codewiki.codewiki.model.Article;
import java.util.Set;

public interface ArticleRepositoryCustom {
    List<Article> findByTagsIds(Set<Long> tagIds);
    List<Article> findComplexSearch(String query, Long categoryId, Set<Long> tagIds);
}