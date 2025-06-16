package com.codewiki.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class SlugGenerator {
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern EDGES_DASHES = Pattern.compile("(^-|-$)");
    private static final Pattern SPECIAL_CHARS = Pattern.compile("[^a-zA-Z0-9\\s-]");

    public static String generateSlug(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        // Нормализация строки (удаление диакритических знаков)
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
            .replaceAll("[^\\p{ASCII}]", "");

        // Удаление специальных символов
        String slug = SPECIAL_CHARS.matcher(normalized).replaceAll("");

        // Замена пробелов на дефисы
        slug = WHITESPACE.matcher(slug).replaceAll("-");

        // Удаление всех не-латинских символов
        slug = NONLATIN.matcher(slug).replaceAll("");

        // Удаление лишних дефисов в начале и конце
        slug = EDGES_DASHES.matcher(slug).replaceAll("");

        // Приведение к нижнему регистру
        slug = slug.toLowerCase(Locale.ENGLISH);

        // Если после обработки строка пустая, генерируем случайный slug
        if (slug.isEmpty()) {
            return "slug-" + System.currentTimeMillis();
        }

        return slug;
    }

    public static String generateSlugWithTimestamp(String input) {
        String slug = generateSlug(input);
        return slug + "-" + System.currentTimeMillis();
    }
}