package fr.univ_eiffel.jdict;

import fr.univ_eiffel.jdict.format.EntryFormatter;
import fr.univ_eiffel.jdict.format.Formatters;

/**
 * Une demande de recherche telle qu'écrite par l'utilisateur :
 * "congres" (format JSON par défaut) ou "yaml:CONGRES" (format YAML).
 */
public record SearchRequest(String word, EntryFormatter formatter) {

    public static SearchRequest parse(String text) {
        int colon = text.indexOf(':');
        if (colon > 0) {
            EntryFormatter formatter = Formatters.get(text.substring(0, colon));
            if (formatter != null) {
                return new SearchRequest(text.substring(colon + 1), formatter);
            }
        }
        return new SearchRequest(text, Formatters.DEFAULT);
    }
}
