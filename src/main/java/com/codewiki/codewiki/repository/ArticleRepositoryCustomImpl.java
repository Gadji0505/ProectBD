package com.codewiki.codewiki.repository;

import com.codewiki.codewiki.model.Article;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.util.List;
import java.util.Set;

public class ArticleRepositoryCustomImpl implements ArticleRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Article> findByTagsIds(Set<Long> tagIds) {
        String jpql = "SELECT DISTINCT a FROM Article a JOIN a.tags t WHERE t.id IN :tagIds";
        TypedQuery<Article> query = entityManager.createQuery(jpql, Article.class);
        query.setParameter("tagIds", tagIds);
        return query.getResultList();
    }

    @Override
    public List<Article> findComplexSearch(String query, Long categoryId, Set<Long> tagIds) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Article> cq = cb.createQuery(Article.class);
        Root<Article> article = cq.from(Article.class);

        // Основной предикат (условие WHERE)
        Predicate predicate = cb.conjunction();

        // Поиск по тексту (в названии или содержании)
        if (query != null && !query.isBlank()) {
            Predicate titlePredicate = cb.like(cb.lower(article.get("title")), "%" + query.toLowerCase() + "%");
            Predicate contentPredicate = cb.like(cb.lower(article.get("content")), "%" + query.toLowerCase() + "%");
            predicate = cb.and(predicate, cb.or(titlePredicate, contentPredicate));
        }

        // Фильтр по категории
        if (categoryId != null) {
            predicate = cb.and(predicate, cb.equal(article.get("category").get("id"), categoryId));
        }

        // Фильтр по тегам
        if (tagIds != null && !tagIds.isEmpty()) {
            Join<Object, Object> tagsJoin = article.join("tags");
            predicate = cb.and(predicate, tagsJoin.get("id").in(tagIds));
            cq.distinct(true);
        }

        cq.where(predicate);
        cq.orderBy(cb.desc(article.get("createdAt"))); // Сортировка по дате создания

        return entityManager.createQuery(cq).getResultList();
    }
}