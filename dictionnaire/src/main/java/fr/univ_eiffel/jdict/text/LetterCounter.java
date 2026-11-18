package fr.univ_eiffel.jdict.text;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Compte les lettres A-Z (et les paires de lettres qui se suivent) dans des textes,
 * puis construit une table de fréquences pour les grilles de Boggle.
 *
 * Digrammes : si une lettre X est presque toujours suivie de la même lettre Y
 * (ex : Q suivi de U en français), on compte "XY" comme un seul symbole.
 */
public class LetterCounter {
    private static final Pattern TEMPLATE = Pattern.compile("\\{\\{[^{}]*}}");
    private static final Pattern LINK = Pattern.compile("\\[\\[(?:[^\\[\\]|]*\\|)?([^\\[\\]|]*)]]");

    /** Une lettre X est le début d'un digramme si elle est suivie de la même lettre au moins 95 % du temps. */
    private static final double DIGRAM_THRESHOLD = 0.95;
    /** On ignore les lettres trop rares pour décider (moins de 100 apparitions). */
    private static final long DIGRAM_MIN_COUNT = 100;

    private final long[] letters = new long[26];          // letters[0] = nombre de A, etc.
    private final long[][] pairs = new long[26][26];      // pairs[x][y] = nombre de fois où x est suivi de y

    /** Enlève le balisage MediaWiki : {{modèles}} supprimés, [[lien|texte]] -> texte, '' supprimés. */
    public static String stripMarkup(String text) {
        String previous;
        do {                                               // les modèles peuvent être imbriqués
            previous = text;
            text = TEMPLATE.matcher(text).replaceAll("");
        } while (!text.equals(previous));
        text = LINK.matcher(text).replaceAll("$1");
        return text.replace("''", "").replaceAll("\\s+", " ").trim();
    }

    /** Ajoute les lettres d'un texte aux compteurs. */
    public void count(String text) {
        String normalized = WordNormalizer.normalize(text);
        int before = -1;                                   // lettre précédente (-1 = pas une lettre)
        for (int i = 0; i < normalized.length(); i++) {
            char c = normalized.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                int letter = c - 'A';
                letters[letter]++;
                if (before != -1) {
                    pairs[before][letter]++;
                }
                before = letter;
            } else {
                before = -1;
            }
        }
    }

    /**
     * Construit la table : symbole -> fréquence pour 10 000 symboles (au moins 1).
     * Les lettres qui forment un digramme sont remplacées par le digramme (ex : Q -> QU).
     */
    public Map<String, Long> table() {
        long[] counts = letters.clone();
        Map<String, Long> raw = new LinkedHashMap<>();
        String[] digramOf = new String[26];

        for (int x = 0; x < 26; x++) {
            if (letters[x] < DIGRAM_MIN_COUNT) {
                continue;
            }
            for (int y = 0; y < 26; y++) {
                if (pairs[x][y] >= DIGRAM_THRESHOLD * letters[x]) {
                    // chaque "XY" utilise un X et un Y : on les retire des lettres seules
                    digramOf[x] = "" + (char) ('A' + x) + (char) ('A' + y);
                    counts[x] -= pairs[x][y];
                    counts[y] -= pairs[x][y];
                    raw.put(digramOf[x], pairs[x][y]);
                }
            }
        }

        Map<String, Long> symbols = new java.util.TreeMap<>();
        long total = 0;
        for (int x = 0; x < 26; x++) {
            if (digramOf[x] != null) {
                symbols.put(digramOf[x], raw.get(digramOf[x]));  // X seul est trop rare : on ne garde que XY
                total += raw.get(digramOf[x]);
            } else if (counts[x] > 0) {
                symbols.put(String.valueOf((char) ('A' + x)), counts[x]);
                total += counts[x];
            }
        }
        Map<String, Long> table = new java.util.TreeMap<>();
        for (Map.Entry<String, Long> e : symbols.entrySet()) {
            long frequency = Math.round(e.getValue() * 10_000.0 / total);
            table.put(e.getKey(), Math.max(1, frequency));
        }
        return table;
    }
}
