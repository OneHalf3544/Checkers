package ru.javatalks.checkers;

import com.google.common.collect.Maps;
import org.apache.commons.io.IOUtils;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Date: 13.11.11
 * Time: 0:20
 *
 * @author OneHalf
 */
public class L10nBundleBundle {

    private final Map<Language, ResourceBundle> bundleMap = Maps.newHashMap();
    
    private ResourceBundle currentBundle;

    public L10nBundleBundle(Language language) throws IOException {
        bundleMap.put(Language.ENGLISH, getBundle(Language.ENGLISH));
        bundleMap.put(Language.RUSSIAN, getBundle(Language.RUSSIAN));
        bundleMap.put(Language.UKRAINIAN, getBundle(Language.UKRAINIAN));

        currentBundle = bundleMap.get(language);
    }

    public String getString(String key) {
        return currentBundle.getString(key);
    }

    public void setLanguage(Language language) {
        currentBundle = bundleMap.get(language);
    }

    private ResourceBundle getBundle(Language language) throws IOException {
        Reader reader = null;
        try {
            reader = new FileReader(language.getPropFile());
            return new PropertyResourceBundle(reader);
        }
        finally {
            IOUtils.closeQuietly(reader);
        }
    }
}
