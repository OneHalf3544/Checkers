package ru.javatalks.checkers.gui.language;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Date: 12.11.11
 * Time: 20:22
 *
 * @author OneHalf
 */
public class Language {

    private static final Logger log = Logger.getLogger(Language.class);

    private final String resourceName;
    private final String nameForMenu;
    private final ResourceBundle bundle;

    Language(String fileName) {
        log.debug("load language from " + fileName);
        this.resourceName = fileName;
        try {
            bundle = loadBundle();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        this.nameForMenu = bundle.getString("languageName");

        log.debug(String.format("lang %s loaded", nameForMenu));
    }

    public String getNameForMenu() {
        return nameForMenu;
    }

    ResourceBundle getBundle() {
        return bundle;
    }

    public String getResourceName() {
        return resourceName;
    }

    private ResourceBundle loadBundle() throws IOException {
        Reader reader = null;
        try {
            reader = new InputStreamReader(Language.class.getResourceAsStream(resourceName));
            return new PropertyResourceBundle(reader);
        }
        finally {
            IOUtils.closeQuietly(reader);
        }
    }

    @Override
    public String toString() {
        return getNameForMenu();
    }
}
