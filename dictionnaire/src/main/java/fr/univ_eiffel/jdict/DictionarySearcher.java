package fr.univ_eiffel.jdict;

import fr.univ_eiffel.jdict.storage.Dictionary;
import fr.univ_eiffel.jdict.storage.DictionaryLocation;

import java.util.List;

/**
 * Recherche un mot dans le dictionnaire.
 * Usage : java fr.univ_eiffel.jdict.DictionarySearcher definitions congres
 *         java fr.univ_eiffel.jdict.DictionarySearcher definitions CONGRES       (forme normalisée)
 *         java fr.univ_eiffel.jdict.DictionarySearcher definitions yaml:CONGRES  (sortie YAML)
 * Valeur de sortie : 0 si au moins un mot est trouvé, 1 sinon.
 */
public class DictionarySearcher {

    public static void main(String[] args) {
        Cli.run(() -> run(args));
    }

    private static void run(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage : DictionarySearcher <fichier> [yaml:]<mot>");
            System.exit(2);
        }
        SearchRequest request = SearchRequest.parse(args[1]);
        boolean found;
        try (Dictionary dictionary = new Dictionary(DictionaryLocation.parse(args[0]))) {
            List<Entry> entries = dictionary.search(request.word());
            for (Entry entry : entries) {
                Output.OUT.println(request.formatter().format(entry));
            }
            found = !entries.isEmpty();
        }
        System.exit(found ? 0 : 1);
    }
}
