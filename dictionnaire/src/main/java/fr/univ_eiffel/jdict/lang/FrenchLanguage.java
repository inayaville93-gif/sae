package fr.univ_eiffel.jdict.lang;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Wiktionary français.
 * - section de langue : "== {{langue|fr}} =="
 * - types de mots : "=== {{S|nom|fr}} ===", "=== {{S|verbe|fr|flexion}} ===",
 *   "=== {{S|adjectif|fr|num=2}} ===", ou l'ancien format "=== {{-nom-|fr}} ==="
 * - les autres sections n'ont pas de code langue : "{{S|étymologie}}", "{{S|synonymes}}"...
 */
public class FrenchLanguage implements Language {
    private static final Pattern LANGUAGE = Pattern.compile("\\{\\{\\s*langue\\s*\\|\\s*fr\\s*}}");
    // {{S|type|fr...}}  : le type est le 1er paramètre, "fr" le 2e
    private static final Pattern SECTION = Pattern.compile("\\{\\{\\s*S\\s*\\|([^|}]+)\\|\\s*fr\\s*[|}]");
    // ancien format {{-nom-|fr}}
    private static final Pattern OLD_SECTION = Pattern.compile("\\{\\{\\s*-([^|}]+)-\\s*\\|\\s*fr\\s*[|}]");

    @Override
    public String code() {
        return "fr";
    }

    @Override
    public boolean isLanguageSection(String sectionTitle) {
        return LANGUAGE.matcher(sectionTitle).find();
    }

    @Override
    public String partOfSpeech(String sectionTitle) {
        Matcher m = SECTION.matcher(sectionTitle);
        if (m.find()) {
            return m.group(1).trim();
        }
        m = OLD_SECTION.matcher(sectionTitle);
        if (m.find()) {
            return m.group(1).trim();
        }
        return null;
    }
}
