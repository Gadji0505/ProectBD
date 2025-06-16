package com.codewiki.util;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.ext.autolink.AutolinkExtension;

import java.util.Arrays;
import java.util.List;

public class MarkdownParser {
    private static final List<Extension> EXTENSIONS = Arrays.asList(
        TablesExtension.create(),
        HeadingAnchorExtension.create(),
        AutolinkExtension.create()
    );

    private static final Parser parser = Parser.builder()
            .extensions(EXTENSIONS)
            .build();

    private static final HtmlRenderer renderer = HtmlRenderer.builder()
            .extensions(EXTENSIONS)
            .escapeHtml(true)
            .softbreak("<br>")
            .build();

    public static String parse(String markdown) {
        if (markdown == null || markdown.trim().isEmpty()) {
            return "";
        }

        Node document = parser.parse(markdown);
        return renderer.render(document);
    }

    public static String parseInline(String markdown) {
        if (markdown == null || markdown.trim().isEmpty()) {
            return "";
        }

        Node document = parser.parse(markdown);
        return renderer.render(document).trim();
    }

    public static String highlightCode(String markdown) {
        // Добавляем подсветку синтаксиса для блоков кода
        return parse(markdown.replaceAll("```(\\w+)?\\n([\\s\\S]*?)\\n```", 
            "<pre><code class=\"language-$1\">$2</code></pre>"));
    }
}