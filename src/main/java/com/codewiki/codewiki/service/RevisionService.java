package com.codewiki.codewiki.service;

import com.codewiki.codewiki.model.Revision;
import com.codewiki.codewiki.repository.RevisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RevisionService {

    private final RevisionRepository revisionRepository;

    @Autowired
    public RevisionService(RevisionRepository revisionRepository) {
        this.revisionRepository = revisionRepository;
    }

    @Transactional(readOnly = true)
    public List<Revision> getAllRevisions() {
        return revisionRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public Optional<Revision> getRevisionById(Long id) {
        return revisionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Revision> getRevisionsByArticleId(Long articleId) {
        return revisionRepository.findByArticleIdOrderByCreatedAtDesc(articleId);
    }

    @Transactional(readOnly = true)
    public Optional<Revision> getLastRevisionForArticle(Long articleId) {
        return revisionRepository.findFirstByArticleIdOrderByCreatedAtDesc(articleId);
    }

    @Transactional(readOnly = true)
    public Optional<Revision> findLatestByArticleId(Long articleId) {
        return revisionRepository.findTopByArticleIdOrderByVersionNumberDesc(articleId);
    }

    @Transactional
    public Revision saveRevision(Revision revision) {
        if (revision.getTitle() == null || revision.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Revision title cannot be empty");
        }
        if (revision.getContent() == null || revision.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Revision content cannot be empty");
        }
        if (revision.getArticle() == null) {
            throw new IllegalArgumentException("Revision must belong to an article");
        }
        if (revision.getEditor() == null) {
            throw new IllegalArgumentException("Revision must have an editor");
        }
        
        if (revision.getVersionNumber() == null) {
            Optional<Revision> lastRevision = findLatestByArticleId(revision.getArticle().getId());
            revision.setVersionNumber(lastRevision.map(rev -> rev.getVersionNumber() + 1).orElse(1));
        }
        
        return revisionRepository.save(revision);
    }

    @Transactional
    public void deleteRevision(Long id) {
        revisionRepository.deleteById(id);
    }

    @Transactional
    public void deleteRevisionsByArticleId(Long articleId) {
        revisionRepository.deleteByArticleId(articleId);
    }

    @Transactional(readOnly = true)
    public List<Revision> getRevisionsByEditorId(Long editorId) {
        return revisionRepository.findByEditorIdOrderByCreatedAtDesc(editorId);
    }

    @Transactional(readOnly = true)
    public List<Revision> findByEditorIdAndArticleId(Long editorId, Long articleId) {
        return revisionRepository.findByEditorIdAndArticleIdOrderByCreatedAtDesc(editorId, articleId);
    }

    @Transactional(readOnly = true)
    public List<Revision> findCurrentRevisions() {
        return revisionRepository.findByIsCurrentTrue();
    }

    @Transactional(readOnly = true)
    public long countRevisionsByArticleId(Long articleId) {
        return revisionRepository.countByArticleId(articleId);
    }

    @Transactional
    public void markRevisionAsCurrent(Long revisionId) {
        Revision revision = revisionRepository.findById(revisionId)
                .orElseThrow(() -> new IllegalArgumentException("Revision not found"));
        
        revisionRepository.clearCurrentFlagForArticle(revision.getArticle().getId());
        
        revision.setIsCurrent(true);
        revisionRepository.save(revision);
    }

    @Transactional(readOnly = true)
    public Optional<Revision> getCurrentRevisionForArticle(Long articleId) {
        return revisionRepository.findByArticleIdAndIsCurrentTrue(articleId);
    }
}