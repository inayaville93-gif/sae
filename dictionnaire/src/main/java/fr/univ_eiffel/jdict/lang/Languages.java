package fr.univ_eiffel.jdict.lang;

import java.util.Map;
import java.util.Set;

/** Les langues disponibles, retrouvées à partir de leur code ("fr", "en"...). */
public final class Languages {
    private static final Map<String, Language> LANGUAGES = Map.of(
            "fr", new FrenchLanguage(),
            "en", new EnglishLanguage());

    private Languages() {
    }

    /** Renvoie la langue de code donné, ou lève une exception si elle n'est pas gérée. */
    public static Language get(String code) {
        Language language = LANGUAGES.get(code);
        if (language == null) {
            throw new IllegalArgumentException("langue non gérée : " + code + " (disponibles : " + codes() + ")");
        }
        return language;
    }

    public static Set<String> codes() {
        return LANGUAGES.keySet();
    }
}
