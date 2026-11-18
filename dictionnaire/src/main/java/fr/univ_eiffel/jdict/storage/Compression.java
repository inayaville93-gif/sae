package fr.univ_eiffel.jdict.storage;

import java.io.IOException;

/**
 * Algorithme de compression des entrées du fichier de définitions.
 * Pour ajouter un algorithme, il suffit d'implémenter cette interface et de
 * l'enregistrer dans {@link Compressions}.
 */
public interface Compression {

    /** Nom écrit dans l'en-tête du fichier (ex : "zlib"), ou null si pas de compression. */
    String name();

    byte[] compress(byte[] data);

    byte[] decompress(byte[] data) throws IOException;
}
