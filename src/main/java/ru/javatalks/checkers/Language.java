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

    RUSSIAN("russian.properties"),
    UKRAINIAN("ukrainian.properties"),
    ENGLISH("english.properties");
    
    private final File file;

    Language(String fileName) {
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
}
