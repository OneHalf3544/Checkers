package ru.javatalks.checkers.gui;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.NoSuchElementException;

/**
 * Date: 12.11.11
 * Time: 20:22
 *
 * @author OneHalf
 */
public enum Language {

    RUSSIAN("/russian.properties", "Русский"),
    UKRAINIAN("/ukrainian.properties", "Украинский"),
    ENGLISH("/english.properties", "English");
    
    private final String resourceName;
    private final String nameForMenu;

    Language(String fileName, String nameForMenu) {
        this.nameForMenu = nameForMenu;
        this.resourceName = fileName;
    }

    public static Language getByCode(String langName) {
        for (Language language : values()) {
            if (language.name().equalsIgnoreCase(langName)) {
                return language;
            }
        }
        throw new NoSuchElementException(String.format("don't has %s language", langName));
    }

    public Reader getReader() {
        return new InputStreamReader(Language.class.getResourceAsStream(resourceName));
    }

    public String getNameForMenu() {
        return nameForMenu;
    }
}
