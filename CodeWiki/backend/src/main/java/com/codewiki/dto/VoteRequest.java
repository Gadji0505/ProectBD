package com.codewiki.dto;

import lombok.Data;

@Data
public class VoteRequest {
    private boolean isUpvote;  // true = лайк, false = дизлайк
}