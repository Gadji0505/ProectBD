package com.codewiki.services;

import com.codewiki.models.Tag;
import com.codewiki.repositories.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepo;

    public Tag createTag(String name) {
        if (tagRepo.existsByName(name)) {
            throw new RuntimeException("Tag already exists");
        }
        Tag tag = new Tag();
        tag.setName(name);
        return tagRepo.save(tag);
    }

    public List<Tag> getPopularTags() {
        return tagRepo.findTop10ByOrderByArticlesCountDesc();
    }
}