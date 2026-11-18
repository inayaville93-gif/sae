package fr.univ_eiffel.jdict;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Une entrée du dictionnaire : un mot et ses définitions, rangées par type de mot.
 * Exemple : title = "plage", definitions = { "nom" : [...], "verbe" : [...] }.
 *
 * C'est exactement ce qui est écrit sur une ligne JSON du fichier de définitions.
 */
public record Entry(String title, Map<String, List<String>> definitions) {
    public Entry {
        Objects.requireNonNull(title);
        Objects.requireNonNull(definitions);
    }
}
