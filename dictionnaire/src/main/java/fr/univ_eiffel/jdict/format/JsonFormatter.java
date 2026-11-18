package fr.univ_eiffel.jdict.format;

import fr.univ_eiffel.jdict.Entry;
import fr.univ_eiffel.jdict.Json;

/** Affiche l'entrée sur une ligne JSON (le format du fichier de définitions). */
public class JsonFormatter implements EntryFormatter {
    @Override
    public String format(Entry entry) {
        return Json.write(entry);
    }
}
