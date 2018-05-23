package com.firecode.dischole.parser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tika.language.LanguageIdentifier;
import com.firecode.dischole.CrawlConfig;
import com.firecode.dischole.Page;
import com.firecode.dischole.exceptions.ParseException;
import com.firecode.dischole.utils.Net;
import com.firecode.dischole.utils.Util;

/**
 * @author Yasser Ganjisaffar
 */
public class Parser {

    private static final Log logger = LogFactory.getLog(Parser.class);

    private final CrawlConfig config;

    private final HtmlParser htmlContentParser;

    public Parser(CrawlConfig config) throws IllegalAccessException, InstantiationException {
        this.config = config;
        this.htmlContentParser = new TikaHtmlParser(config);
    }

    public Parser(CrawlConfig config, HtmlParser htmlParser) {
        this.config = config;
        this.htmlContentParser = htmlParser;
    }

    public void parse(Page page, String contextURL)
        throws NotAllowedContentException, ParseException {
        if (Util.hasBinaryContent(page.getContentType())) { // BINARY
            BinaryParseData parseData = new BinaryParseData();
            if (config.isIncludeBinaryContentInCrawling()) {
                if (config.isProcessBinaryContentInCrawling()) {
                    parseData.setBinaryContent(page.getContentData());
                } else {
                    parseData.setHtml("<html></html>");
                }
                page.setParseData(parseData);
                if (parseData.getHtml() == null) {
                    throw new ParseException();
                }
                parseData.setOutgoingUrls(Net.extractUrls(parseData.getHtml()));
            } else {
                throw new NotAllowedContentException();
            }
        } else if (Util.hasPlainTextContent(page.getContentType())) { // plain Text
            try {
                TextParseData parseData = new TextParseData();
                if (page.getContentCharset() == null) {
                    parseData.setTextContent(new String(page.getContentData()));
                } else {
                    parseData.setTextContent(
                        new String(page.getContentData(), page.getContentCharset()));
                }
                parseData.setOutgoingUrls(Net.extractUrls(parseData.getTextContent()));
                page.setParseData(parseData);
            } catch (Exception e) {
                logger.error(String.format("{}, while parsing: {}", e.getMessage(), page.getWebURL().getURL()));
                throw new ParseException();
            }
        } else { // isHTML

            HtmlParseData parsedData = this.htmlContentParser.parse(page, contextURL);

            if (page.getContentCharset() == null) {
                page.setContentCharset(parsedData.getContentCharset());
            }

            // Please note that identifying language takes less than 10 milliseconds
            LanguageIdentifier languageIdentifier = new LanguageIdentifier(parsedData.getText());
            page.setLanguage(languageIdentifier.getLanguage());

            page.setParseData(parsedData);

        }
    }
}
