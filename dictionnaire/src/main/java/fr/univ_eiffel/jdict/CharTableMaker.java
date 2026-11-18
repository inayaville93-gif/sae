package fr.univ_eiffel.jdict;

import fr.univ_eiffel.jdict.storage.Dictionary;
import fr.univ_eiffel.jdict.storage.DictionaryLocation;
import fr.univ_eiffel.jdict.text.LetterCounter;

import java.util.List;
import java.util.Map;

/**
 * Génère la table de fréquences des lettres (titres + définitions de toutes les entrées),
 * au format attendu par le moteur de jeu en C (grid_build) : "A 734" ... "QU 98" ...
 * Usage : java fr.univ_eiffel.jdict.CharTableMaker definitions > chartable.txt
 */
public class CharTableMaker {

    public static void main(String[] args) {
        Cli.run(() -> run(args));
    }

    private static void run(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage : CharTableMaker <fichier>");
            System.exit(2);
        }
        LetterCounter counter = new LetterCounter();
        try (Dictionary dictionary = new Dictionary(DictionaryLocation.parse(args[0]))) {
            for (int i = 0; i < dictionary.size(); i++) {
                Entry entry = dictionary.read(i);
                counter.count(entry.title());
                for (List<String> definitions : entry.definitions().values()) {
                    for (String definition : definitions) {
                        counter.count(LetterCounter.stripMarkup(definition));
                    }
                }
            }
        }
        for (Map.Entry<String, Long> e : counter.table().entrySet()) {
            Output.OUT.println(e.getKey() + " " + e.getValue());
        }
    }
}
