package com.codewiki.models;

public enum UserRole {
    ADMIN,       // Полные права
    MODERATOR,   // Может банить, править статьи
    EDITOR,      // Может создавать/редактировать статьи
    BANNED       // Заблокированный пользователь
}