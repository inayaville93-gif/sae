package fr.univ_eiffel.jdict.storage;

import java.util.Map;

/** Les algorithmes de compression disponibles, retrouvés par leur nom. */
public final class Compressions {
    public static final Compression NONE = new NoCompression();
    private static final Map<String, Compression> BY_NAME = Map.of("zlib", new ZlibCompression());

    private Compressions() {
    }

    /** Renvoie l'algorithme de nom donné ; null veut dire "pas de compression". */
    public static Compression get(String name) {
        if (name == null) {
            return NONE;
        }
        Compression compression = BY_NAME.get(name);
        if (compression == null) {
            throw new IllegalArgumentException("compression inconnue : " + name);
        }
        return compression;
    }
}
