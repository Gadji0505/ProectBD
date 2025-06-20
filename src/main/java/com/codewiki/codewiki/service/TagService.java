package com.codewiki.codewiki.service;

import com.codewiki.codewiki.model.Tag;
import com.codewiki.codewiki.repository.TagRepository;
import com.codewiki.codewiki.util.MarkdownUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TagService {

    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Transactional(readOnly = true)
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Tag> getTagById(Long id) {
        return tagRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Tag> findByName(String name) {
        return tagRepository.findByNameIgnoreCase(name);
    }

    @Transactional(readOnly = true)
    public Optional<Tag> getTagBySlug(String slug) {
        return tagRepository.findBySlug(slug);
    }

    @Transactional
    public Tag saveTag(Tag tag) {
        if (tag.getName() == null || tag.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tag name cannot be empty");
        }

        if (tag.getSlug() == null || tag.getSlug().isEmpty()) {
            tag.setSlug(MarkdownUtil.toSlug(tag.getName()));
        }

        Optional<Tag> existingTag = tagRepository.findByNameIgnoreCase(tag.getName());
        if (existingTag.isPresent() && (tag.getId() == null || !existingTag.get().getId().equals(tag.getId()))) {
            throw new IllegalArgumentException("Tag with name '" + tag.getName() + "' already exists.");
        }

        Optional<Tag> existingSlugTag = tagRepository.findBySlug(tag.getSlug());
        if (existingSlugTag.isPresent() && (tag.getId() == null || !existingSlugTag.get().getId().equals(tag.getId()))) {
            tag.setSlug(tag.getSlug() + "-" + System.currentTimeMillis());
        }

        return tagRepository.save(tag);
    }

    @Transactional
    public Tag findOrCreateTag(String name) {
        return findByName(name)
                .orElseGet(() -> {
                    Tag newTag = new Tag();
                    newTag.setName(name);
                    return saveTag(newTag);
                });
    }

    @Transactional
    public void deleteTag(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new IllegalArgumentException("Tag not found with id: " + id);
        }
        tagRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Tag> findAllById(Set<Long> ids) {
        return tagRepository.findAllById(ids);
    }

    @Transactional(readOnly = true)
    public List<Tag> getPopularTags(int limit) {
        return tagRepository.findPopularTags(limit);
    }

    @Transactional(readOnly = true)
    public long getTotalTagsCount() {
        return tagRepository.count();
    }

    @Transactional(readOnly = true)
    public Set<Tag> findOrCreateTags(Set<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        if (tagNames != null) {
            for (String name : tagNames) {
                tags.add(findOrCreateTag(name));
            }
        }
        return tags;
    }
}