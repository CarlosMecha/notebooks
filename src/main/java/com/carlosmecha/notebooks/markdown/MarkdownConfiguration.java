package com.carlosmecha.notebooks.markdown;

import com.vladsch.flexmark.ext.emoji.EmojiExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.options.DataHolder;
import com.vladsch.flexmark.util.options.MutableDataHolder;
import com.vladsch.flexmark.util.options.MutableDataSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Markdown configuration for rendering comments.
 */
@Configuration
public class MarkdownConfiguration {

    @Bean
    public DataHolder markdownOptions() {
        MutableDataHolder options = new MutableDataSet();
        options.setFrom(ParserEmulationProfile.GITHUB_DOC);
        options.set(Parser.EXTENSIONS, Arrays.asList(EmojiExtension.create()));
        return options;
    }

    @Bean
    public Parser markdownParser(DataHolder markdownOptions) {
        return Parser.builder(markdownOptions).build();
    }

    @Bean
    public HtmlRenderer markdownRenderer(DataHolder markdownOptions) {
        return HtmlRenderer.builder(markdownOptions).build();
    }

}
