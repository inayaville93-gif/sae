package fr.univ_eiffel.jdict;

import fr.univ_eiffel.jdict.storage.Dictionary;
import fr.univ_eiffel.jdict.storage.DictionaryLocation;
import fr.univ_eiffel.jdict.text.WordNormalizer;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Affiche les formes normalisées (triées, sans doublon) de tous les mots du dictionnaire.
 * Usage : java fr.univ_eiffel.jdict.NormalizedExtractor definitions > words.txt
 *         java fr.univ_eiffel.jdict.NormalizedExtractor definitions "nom propre,adjectif" > words.txt
 */
public class NormalizedExtractor {

    public static void main(String[] args) {
        Cli.run(() -> run(args));
    }

    private static void run(String[] args) throws Exception {
        if (args.length < 1 || args.length > 2) {
            System.err.println("Usage : NormalizedExtractor <fichier> [\"type1,type2,...\"]");
            System.exit(2);
        }
        // types de mots demandés (null = tous)
        Set<String> types = null;
        if (args.length == 2) {
            types = Arrays.stream(args[1].split(","))
                    .map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toSet());
        }

        try (Dictionary dictionary = new Dictionary(DictionaryLocation.parse(args[0]));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new java.io.FileOutputStream(java.io.FileDescriptor.out), StandardCharsets.UTF_8))) {
            String previous = null;
            // l'index est trié par forme normalisée : les doublons sont donc côte à côte
            for (int i = 0; i < dictionary.size(); i++) {
                Entry entry = dictionary.read(i);
                if (types != null && entry.definitions().keySet().stream().noneMatch(types::contains)) {
                    continue;
                }
                String normalized = WordNormalizer.normalize(entry.title());
                if (!normalized.equals(previous)) {
                    out.write(normalized);
                    out.newLine();
                    previous = normalized;
                }
            }
        }
    }
}
