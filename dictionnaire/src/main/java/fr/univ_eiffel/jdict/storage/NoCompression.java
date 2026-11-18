package fr.univ_eiffel.jdict.storage;

/** Pas de compression : les entrées sont des lignes JSON lisibles. */
public class NoCompression implements Compression {
    @Override
    public String name() {
        return null;
    }

    @Override
    public byte[] compress(byte[] data) {
        return data;
    }

    @Override
    public byte[] decompress(byte[] data) {
        return data;
    }
}
