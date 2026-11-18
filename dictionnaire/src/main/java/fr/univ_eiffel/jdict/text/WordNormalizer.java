package fr.univ_eiffel.jdict.text;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.Locale;

/**
 * Normalisation des mots : majuscules et suppression des diacritiques (accents, cédilles...).
 * Exemple : "congrès" -> "CONGRES", "cœur" -> "COEUR".
 */
public final class WordNormalizer {

    /**
     * Ordre utilisé pour trier l'index :
     * 1. d'abord l'ordre des formes normalisées ("congres" et "congrès" sont à égalité),
     * 2. en cas d'égalité, l'ordre naturel des String (codes Unicode) : "congres" < "congrès".
     */
    public static final Comparator<String> ORDER =
            Comparator.comparing(WordNormalizer::normalize).thenComparing(Comparator.naturalOrder());

    private WordNormalizer() {
    }

    public static String normalize(String word) {
        // NFD sépare chaque lettre de ses accents : "è" devient "e" + "accent grave"
        String decomposed = Normalizer.normalize(word, Normalizer.Form.NFD);
        // on supprime les accents (catégorie Unicode "Mark")
        String withoutMarks = decomposed.replaceAll("\\p{M}", "");
        // les ligatures ne sont pas décomposées par NFD : on les traite à la main
        String withoutLigatures = withoutMarks
                .replace("œ", "oe").replace("Œ", "OE")
                .replace("æ", "ae").replace("Æ", "AE");
        return withoutLigatures.toUpperCase(Locale.ROOT);
    }

    /** Vrai si le mot est déjà sous forme normalisée (ex : "CONGRES"). */
    public static boolean isNormalized(String word) {
        return word.equals(normalize(word));
    }
}
