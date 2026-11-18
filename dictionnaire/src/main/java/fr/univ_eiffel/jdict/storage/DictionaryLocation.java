package fr.univ_eiffel.jdict.storage;

import java.nio.file.Path;

/**
 * Emplacement d'un dictionnaire à partir du nom donné en ligne de commande.
 * "definitions"            -> definitions.dict + definitions.index, sans compression
 * "compressed:definitions" -> les mêmes fichiers, avec compression zlib
 */
public record DictionaryLocation(Path dictFile, Path indexFile, Compression compression) {
    private static final String COMPRESSED_PREFIX = "compressed:";

    public static DictionaryLocation parse(String argument) {
        Compression compression = Compressions.NONE;
        String base = argument;
        if (base.startsWith(COMPRESSED_PREFIX)) {
            base = base.substring(COMPRESSED_PREFIX.length());
            compression = Compressions.get("zlib");
        }
        return new DictionaryLocation(Path.of(base + ".dict"), Path.of(base + ".index"), compression);
    }
}
