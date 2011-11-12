package ru.javatalks.checkers;

import org.apache.commons.io.IOUtils;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Date: 13.11.11
 * Time: 0:20
 *
 * @author OneHalf
 */
public class L10nBundleFactory {

    private L10nBundleFactory() {
    }

    public static ResourceBundle getBundle(Language language) throws IOException {
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
