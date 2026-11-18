package fr.univ_eiffel.jdict.format;

import java.util.Map;

/** Les formats d'affichage disponibles, retrouvés par leur nom. */
public final class Formatters {
    public static final EntryFormatter DEFAULT = new JsonFormatter();
    private static final Map<String, EntryFormatter> BY_NAME = Map.of(
            "json", DEFAULT,
            "yaml", new YamlFormatter());

    private Formatters() {
    }

    /** Renvoie le format de nom donné, ou null s'il n'existe pas. */
    public static EntryFormatter get(String name) {
        return BY_NAME.get(name);
    }
}
