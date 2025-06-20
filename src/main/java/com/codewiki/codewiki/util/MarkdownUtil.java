package com.codewiki.codewiki.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownUtil {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    // =========================================================================
    // ВНИМАНИЕ: ЭТО ОЧЕНЬ ПРОСТОЙ, НАИВНЫЙ И ПОТЕНЦИАЛЬНО НЕБЕЗОПАСНЫЙ ПАРСЕР MARKDOWN!
    // Он не предназначен для использования в продакшн-системах, доступных извне.
    // Используйте его только для локальных нужд с полным пониманием рисков.
    // =========================================================================

    public static String markdownToHtml(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return "";
        }

        String html = markdown;

        // Экранирование HTML-специальных символов для базовой безопасности (очень примитивное)
        html = html.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");

        // Заголовки (H1, H2, H3)
        // Порядок важен: сначала H3, потом H2, потом H1
        html = html.replaceAll("###\\s*(.+)", "<h3>$1</h3>");
        html = html.replaceAll("##\\s*(.+)", "<h2>$1</h2>");
        html = html.replaceAll("#\\s*(.+)", "<h1>$1</h1>");

        // Жирный текст: **текст** или __текст__
        html = html.replaceAll("\\*\\*(.*?)\\*\\*", "<strong>$1</strong>");
        html = html.replaceAll("__(.*?)__", "<strong>$1</strong>");

        // Курсив: *текст* или _текст_
        html = html.replaceAll("\\*(.*?)\\*", "<em>$1</em>");
        html = html.replaceAll("_(.*?)_", "<em>$1</em>");

        // Ссылки: [текст ссылки](URL)
        // Используем Pattern и Matcher для обработки ссылок, чтобы не заменять уже обработанные * и **
        Pattern linkPattern = Pattern.compile("\\[(.*?)\\]\\((.*?)\\)");
        Matcher linkMatcher = linkPattern.matcher(html);
        StringBuffer sb = new StringBuffer();
        while (linkMatcher.find()) {
            // Экранирование URL в ссылке
            String url = linkMatcher.group(2).replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&");
            linkMatcher.appendReplacement(sb, "<a href=\"" + url + "\">" + linkMatcher.group(1) + "</a>");
        }
        linkMatcher.appendTail(sb);
        html = sb.toString();

        // Абзацы и переносы строк
        // 1. Заменяем двойные переносы строк на маркеры абзацев
        html = html.replaceAll("\\n\\n", "||PARAGRAPH_BREAK||");
        // 2. Заменяем одинарные переносы строк на <br/>
        html = html.replaceAll("\\n", "<br/>");
        // 3. Заменяем маркеры абзацев на теги <p>
        html = html.replaceAll("\\|\\|PARAGRAPH_BREAK\\|\\|", "</p><p>");

        // Оборачиваем весь текст в абзацы, если он не начинается с блочного элемента
        // Это упрощение, которое может привести к проблемам с вложенностью,
        // но для базовой задачи вполне подойдет.
        if (!html.startsWith("<p>") && !html.startsWith("<h")) {
            html = "<p>" + html + "</p>";
        }

        return html;
    }

    // НОВЫЙ СТАТИЧЕСКИЙ МЕТОД: для генерации слага
    public static String toSlug(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        String nowhitespace = WHITESPACE.matcher(text).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }
}