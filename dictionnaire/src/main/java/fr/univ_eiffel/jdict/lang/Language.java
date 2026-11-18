package fr.univ_eiffel.jdict.lang;

/**
 * Ce qui change d'un Wiktionary à l'autre : la façon d'écrire les titres de sections.
 * Pour ajouter une nouvelle langue, il suffit d'écrire une nouvelle classe qui
 * implémente cette interface et de l'enregistrer dans {@link Languages}.
 */
public interface Language {

    /** Code de la langue, ex : "fr" ou "en". */
    String code();

    /**
     * Vrai si le titre de section (niveau 2, ex : "{{langue|fr}}") annonce la partie
     * de la page consacrée à cette langue.
     */
    boolean isLanguageSection(String sectionTitle);

    /**
     * Si le titre de section correspond à un type de mot (nom, verbe...), renvoie ce type
     * (ex : "nom"). Sinon (étymologie, prononciation, traductions...), renvoie null.
     */
    String partOfSpeech(String sectionTitle);
}
