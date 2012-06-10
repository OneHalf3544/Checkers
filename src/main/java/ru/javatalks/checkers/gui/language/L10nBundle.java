package ru.javatalks.checkers.gui.language;

/**
 * Date: 13.11.11
 * Time: 0:20
 *
 * @author OneHalf
 */
public class L10nBundle {

    private Language currentBundle;

    private Language[] languages;

    public L10nBundle(String language, String bundleNames) {
        setLanguageBundles(bundleNames.split("\\s*,\\s*"));

        for (Language lang : languages) {
            if (lang.getResourceName().equalsIgnoreCase(language+".properties")) {
                setLanguage(lang);
                return;
            }
        }
    }

    private void setLanguageBundles(String[] languageBundles) {
        languages = new Language[languageBundles.length];
        for (int i = 0; i < languageBundles.length; i++) {
            languages[i] = new Language(String.format("%s.properties", languageBundles[i]));
        }
    }

    public String getString(String key) {
        return currentBundle.getBundle().getString(key);
    }

    public void setLanguage(Language language) {
        currentBundle = language;
    }

    public Language[] getLanguages() {
        return languages;
    }

    public Language getCurrentLanguage() {
        return currentBundle;
    }
}
