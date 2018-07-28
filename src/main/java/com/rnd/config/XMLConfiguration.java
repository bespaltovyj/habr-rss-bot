package com.rnd.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

@Configuration
public class XMLConfiguration {

    @Bean
    public DocumentBuilderFactory documentBuilderFactory() {
        return DocumentBuilderFactory.newInstance();
    }

    @Bean
    public DocumentBuilder documentBuilder() throws ParserConfigurationException {
        return documentBuilderFactory().newDocumentBuilder();
    }
}
