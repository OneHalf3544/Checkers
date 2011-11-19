package ru.javatalks.checkers;

import java.io.File;
import java.util.NoSuchElementException;

/**
 * Date: 12.11.11
 * Time: 20:22
 *
 * @author OneHalf
 */
public enum Language {

    RUSSIAN("resources/russian.properties", "Русский"),
    UKRAINIAN("resources/ukrainian.properties", "Украинский"),
    ENGLISH("resources/english.properties", "English");
    
    private final File file;
    private final String nameForMenu;

    Language(String fileName, String nameForMenu) {
        this.nameForMenu = nameForMenu;
        this.file = new File(fileName);
    }

    public static Language getByCode(String langName) {
        for (Language language : values()) {
            if (language.name().equalsIgnoreCase(langName)) {
                return language;
            }
        }
        throw new NoSuchElementException(String.format("don't has %s language", langName));
    }

    public File getPropFile() {
        return file;
    }

    public String getNameForMenu() {
        return nameForMenu;
    }
}
