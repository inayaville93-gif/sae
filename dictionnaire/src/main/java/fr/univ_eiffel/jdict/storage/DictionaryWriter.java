package fr.univ_eiffel.jdict.storage;

import fr.univ_eiffel.jdict.Entry;
import fr.univ_eiffel.jdict.Json;
import fr.univ_eiffel.jdict.text.WordNormalizer;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Écrit le fichier de définitions (.dict) et, à la fermeture, son index (.index).
 *
 * Fichier .dict : une ligne JSON d'en-tête, puis une entrée par mot
 *   (ligne JSON, ou tableau d'octets compressé si la compression est activée).
 * Fichier .index : "DICTINDX" puis, pour chaque mot dans l'ordre trié,
 *   la position du premier et du dernier octet de son entrée (2 entiers de 4 octets, grand-boutiste).
 */
public class DictionaryWriter implements Closeable {
    public static final byte[] INDEX_MAGIC = "DICTINDX".getBytes(StandardCharsets.US_ASCII);
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");

    /** Ce qu'on retient de chaque mot pour construire l'index. */
    private record Position(String title, String normalized, long start, long end) {
    }

    private static final Comparator<Position> ORDER =
            Comparator.comparing(Position::normalized).thenComparing(Position::title);

    private final DictionaryLocation location;
    private final OutputStream dict;
    private final List<Position> positions = new ArrayList<>();
    private long written;      // nombre d'octets déjà écrits dans le .dict

    public DictionaryWriter(DictionaryLocation location, String languageCode) throws IOException {
        this.location = location;
        this.dict = new BufferedOutputStream(Files.newOutputStream(location.dictFile()), 1 << 16);

        Map<String, Object> header = new LinkedHashMap<>();
        header.put("description", "definition file");
        header.put("created_on", ZonedDateTime.now(ZoneOffset.UTC).format(DATE));
        header.put("language", languageCode);
        if (location.compression().name() != null) {
            header.put("compression", location.compression().name());
        }
        write((Json.write(header) + "\n").getBytes(StandardCharsets.UTF_8));
    }

    /** Ajoute une entrée à la fin du fichier de définitions. */
    public void add(Entry entry) throws IOException {
        byte[] json = Json.write(entry).getBytes(StandardCharsets.UTF_8);
        byte[] stored = location.compression().compress(json);
        long start = written;
        write(stored);
        long end = written - 1;                         // position du dernier octet de l'entrée
        if (location.compression().name() == null) {
            write(new byte[] {'\n'});                   // sans compression : une entrée par ligne
        }
        if (end > Integer.MAX_VALUE) {
            throw new IOException("fichier de définitions trop gros pour des positions sur 4 octets");
        }
        positions.add(new Position(entry.title(), WordNormalizer.normalize(entry.title()), start, end));
    }

    private void write(byte[] bytes) throws IOException {
        dict.write(bytes);
        written += bytes.length;
    }

    public int size() {
        return positions.size();
    }

    /** Ferme le .dict, puis trie les positions et écrit l'index. */
    @Override
    public void close() throws IOException {
        dict.close();
        positions.sort(ORDER);
        try (DataOutputStream index = new DataOutputStream(
                new BufferedOutputStream(Files.newOutputStream(location.indexFile()), 1 << 16))) {
            index.write(INDEX_MAGIC);
            for (Position p : positions) {
                index.writeInt((int) p.start());       // DataOutputStream écrit en grand-boutiste
                index.writeInt((int) p.end());
            }
        }
    }
}
