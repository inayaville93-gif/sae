package fr.univ_eiffel.jdict.storage;

import fr.univ_eiffel.jdict.Entry;
import fr.univ_eiffel.jdict.Json;
import fr.univ_eiffel.jdict.text.WordNormalizer;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Lecture d'un dictionnaire (fichiers .dict et .index) avec RandomAccessFile :
 * on ne lit que les morceaux de fichier dont on a besoin.
 *
 * Les mots sont numérotés de 0 à size()-1 dans l'ordre de l'index (ordre trié).
 */
public class Dictionary implements Closeable {
    private final RandomAccessFile dict;
    private final RandomAccessFile index;
    private final int size;
    private final Map<String, Object> header;
    private final Compression compression;

    public Dictionary(DictionaryLocation location) throws IOException {
        this.dict = new RandomAccessFile(location.dictFile().toFile(), "r");
        try {
            this.index = new RandomAccessFile(location.indexFile().toFile(), "r");
        } catch (IOException e) {
            dict.close();
            throw e;
        }
        byte[] magic = new byte[DictionaryWriter.INDEX_MAGIC.length];
        if (index.length() < magic.length) {
            close();
            throw new IOException("fichier d'index invalide");
        }
        index.readFully(magic);
        if (!Arrays.equals(magic, DictionaryWriter.INDEX_MAGIC)) {
            close();
            throw new IOException("fichier d'index invalide (en-tête DICTINDX absent)");
        }
        this.size = (int) ((index.length() - magic.length) / 8);

        // l'en-tête du .dict dit si les entrées sont compressées
        this.header = Json.readMap(readHeaderLine());
        this.compression = Compressions.get((String) header.get("compression"));
    }

    private String readHeaderLine() throws IOException {
        dict.seek(0);
        ByteArrayOutputStream line = new ByteArrayOutputStream();
        int b;
        while ((b = dict.read()) != -1 && b != '\n') {
            line.write(b);
        }
        return line.toString(StandardCharsets.UTF_8);
    }

    /** Nombre de mots dans le dictionnaire. */
    public int size() {
        return size;
    }

    /** Langue du dictionnaire ("fr", "en"...). */
    public String language() {
        return (String) header.get("language");
    }

    /** Lit la ligne JSON du mot numéro i (dans l'ordre de l'index). */
    public String readJson(int i) throws IOException {
        index.seek(DictionaryWriter.INDEX_MAGIC.length + 8L * i);
        long start = Integer.toUnsignedLong(index.readInt());
        long end = Integer.toUnsignedLong(index.readInt());
        byte[] bytes = new byte[(int) (end - start + 1)];
        dict.seek(start);
        dict.readFully(bytes);
        return new String(compression.decompress(bytes), StandardCharsets.UTF_8);
    }

    /** Lit l'entrée du mot numéro i. */
    public Entry read(int i) throws IOException {
        return Json.readEntry(readJson(i));
    }

    /**
     * Recherche par dichotomie : renvoie le numéro du premier mot qui n'est pas
     * avant "word" dans l'ordre de l'index (ou size() si tous sont avant).
     * Si normalizedOnly est vrai, on compare seulement les formes normalisées.
     */
    private int lowerBound(String word, boolean normalizedOnly) throws IOException {
        int low = 0;
        int high = size;              // on cherche dans [low, high[
        while (low < high) {
            int middle = (low + high) >>> 1;
            String title = read(middle).title();
            int cmp = normalizedOnly
                    ? WordNormalizer.normalize(title).compareTo(word)
                    : WordNormalizer.ORDER.compare(title, word);
            if (cmp < 0) {
                low = middle + 1;     // le mot du milieu est avant : on cherche à droite
            } else {
                high = middle;        // sinon on cherche à gauche (en gardant le milieu)
            }
        }
        return low;
    }

    /** Renvoie l'entrée du mot exact, ou null s'il n'est pas dans le dictionnaire. */
    public Entry find(String word) throws IOException {
        int i = lowerBound(word, false);
        if (i < size) {
            Entry entry = read(i);
            if (entry.title().equals(word)) {
                return entry;
            }
        }
        return null;
    }

    /** Renvoie toutes les entrées dont la forme normalisée vaut "normalized" (ex : CONGRES). */
    public List<Entry> findNormalized(String normalized) throws IOException {
        List<Entry> result = new ArrayList<>();
        for (int i = lowerBound(normalized, true); i < size; i++) {
            Entry entry = read(i);
            if (!WordNormalizer.normalize(entry.title()).equals(normalized)) {
                break;                // les mots sont triés : plus aucun ne correspond
            }
            result.add(entry);
        }
        return result;
    }

    /**
     * Recherche telle que demandée par l'énoncé : un mot en majuscules est considéré comme
     * une forme normalisée (on renvoie tous les mots correspondants), sinon on cherche le mot exact.
     */
    public List<Entry> search(String word) throws IOException {
        if (WordNormalizer.isNormalized(word)) {
            return findNormalized(word);
        }
        Entry entry = find(word);
        return entry == null ? List.of() : List.of(entry);
    }

    @Override
    public void close() throws IOException {
        try {
            dict.close();
        } finally {
            if (index != null) {
                index.close();
            }
        }
    }
}
