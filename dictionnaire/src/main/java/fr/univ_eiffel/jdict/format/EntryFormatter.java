package fr.univ_eiffel.jdict.format;

import fr.univ_eiffel.jdict.Entry;

/**
 * Format d'affichage d'une entrée (JSON, YAML...).
 * Pour ajouter un format, il suffit d'implémenter cette interface et de l'enregistrer
 * dans {@link Formatters}.
 */
public interface EntryFormatter {
    String format(Entry entry);
}
