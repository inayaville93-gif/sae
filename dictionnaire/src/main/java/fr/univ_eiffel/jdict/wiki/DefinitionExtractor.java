package fr.univ_eiffel.jdict.wiki;

import fr.univ_eiffel.jdict.Entry;
import fr.univ_eiffel.jdict.lang.Language;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extrait les définitions d'une page MediaWiki pour une langue donnée.
 *
 * On lit le texte ligne par ligne :
 * - "== titre ==" (niveau 2) : début de la section d'une langue ;
 * - dans la section de notre langue, "=== titre ===" (niveau 3 ou plus) : si c'est un
 *   type de mot (nom, verbe...), les définitions qui suivent lui appartiennent ;
 * - une ligne commençant par "#" est une définition, sauf "#*" (exemple) et "#:" (note).
 */
public class DefinitionExtractor {
    private static final Pattern HEADING = Pattern.compile("^(=+)\\s*(.*?)\\s*\\1\\s*$");

    private final Language language;

    public DefinitionExtractor(Language language) {
        this.language = language;
    }

    /** Renvoie l'entrée du mot, ou null si la page ne contient aucune définition dans notre langue. */
    public Entry extract(WikiPage page) {
        Map<String, List<String>> definitions = new LinkedHashMap<>();
        boolean inLanguage = false;        // sommes-nous dans la section de notre langue ?
        String partOfSpeech = null;        // type de mot de la section en cours (ou null)

        for (String line : page.text().split("\n")) {
            Matcher heading = HEADING.matcher(line);
            if (heading.matches()) {
                int level = heading.group(1).length();
                String title = heading.group(2);
                if (level <= 2) {
                    // nouvelle langue : on regarde si c'est la nôtre
                    inLanguage = language.isLanguageSection(title);
                    partOfSpeech = null;
                } else if (inLanguage) {
                    partOfSpeech = language.partOfSpeech(title);
                }
                continue;
            }
            if (inLanguage && partOfSpeech != null && isDefinition(line)) {
                String definition = line.replaceFirst("^#+", "").trim();
                if (!definition.isEmpty()) {
                    definitions.computeIfAbsent(partOfSpeech, k -> new ArrayList<>()).add(definition);
                }
            }
        }
        if (definitions.isEmpty()) {
            return null;
        }
        return new Entry(page.title(), definitions);
    }

    /** "# texte" ou "## texte" sont des définitions ; "#*" (exemples) et "#:" (notes) non. */
    private static boolean isDefinition(String line) {
        if (!line.startsWith("#")) {
            return false;
        }
        String rest = line.replaceFirst("^#+", "");
        return !rest.startsWith("*") && !rest.startsWith(":");
    }
}
