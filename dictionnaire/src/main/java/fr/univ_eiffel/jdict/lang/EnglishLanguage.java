package fr.univ_eiffel.jdict.lang;

import java.util.Locale;
import java.util.Set;

/**
 * Wiktionary anglais.
 * - section de langue : "==English=="
 * - types de mots : "===Noun===" ou "====Verb====" (sous "===Etymology 1===")
 * Les autres sections (Etymology, Pronunciation, Synonyms...) sont ignorées.
 */
public class EnglishLanguage implements Language {
    private static final Set<String> PARTS_OF_SPEECH = Set.of(
            "noun", "proper noun", "verb", "adjective", "adverb", "pronoun", "preposition",
            "conjunction", "interjection", "determiner", "article", "numeral", "number",
            "particle", "prefix", "suffix", "infix", "interfix", "circumfix", "affix",
            "phrase", "prepositional phrase", "proverb", "idiom", "contraction",
            "abbreviation", "initialism", "acronym", "symbol", "letter", "participle",
            "postposition", "classifier", "punctuation mark");

    @Override
    public String code() {
        return "en";
    }

    @Override
    public boolean isLanguageSection(String sectionTitle) {
        return sectionTitle.trim().equals("English");
    }

    @Override
    public String partOfSpeech(String sectionTitle) {
        String type = sectionTitle.trim().toLowerCase(Locale.ROOT);
        if (PARTS_OF_SPEECH.contains(type)) {
            return type;
        }
        return null;
    }
}
