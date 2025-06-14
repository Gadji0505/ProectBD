package com.codewiki.services;

import com.codewiki.models.Draft;
import com.codewiki.models.User;
import com.codewiki.repositories.DraftRepository;
import com.codewiki.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DraftService {

    private final DraftRepository draftRepo;
    private final UserRepository userRepo;

    public Draft saveDraft(String content, Long authorId) {
        User author = userRepo.findById(authorId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Draft draft = new Draft();
        draft.setContent(content);
        draft.setAuthor(author);
        return draftRepo.save(draft);
    }

    public List<Draft> getUserDrafts(Long authorId) {
        return draftRepo.findByAuthorId(authorId);
    }
}