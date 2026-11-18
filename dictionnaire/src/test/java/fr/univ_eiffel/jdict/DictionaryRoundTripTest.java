package fr.univ_eiffel.jdict;

import fr.univ_eiffel.jdict.storage.Dictionary;
import fr.univ_eiffel.jdict.storage.DictionaryLocation;
import fr.univ_eiffel.jdict.storage.DictionaryWriter;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DictionaryRoundTripTest {

    @TempDir
    Path dir;

    private static Entry entry(String title, String definition) {
        return new Entry(title, Map.of("nom", List.of(definition)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "compressed:"})
    void writeThenSearch(String prefix) throws Exception {
        DictionaryLocation location = DictionaryLocation.parse(prefix + dir.resolve("definitions"));
        try (DictionaryWriter writer = new DictionaryWriter(location, "fr")) {
            writer.add(entry("congrès", "Assemblée."));
            writer.add(entry("plage", "Étendue de sable."));
            writer.add(entry("congres", "Pluriel de congre."));
            writer.add(entry("Congrès", "Parlement."));
            writer.add(entry("chat", "Félin."));
        }

        // l'index commence par DICTINDX, puis 8 octets par mot
        byte[] index = Files.readAllBytes(location.indexFile());
        assertEquals("DICTINDX", new String(index, 0, 8, StandardCharsets.US_ASCII));
        assertEquals(8 + 5 * 8, index.length);
        ByteBuffer positions = ByteBuffer.wrap(index, 8, index.length - 8);   // grand-boutiste par défaut
        assertTrue(positions.getInt() < positions.getInt());

        try (Dictionary dictionary = new Dictionary(location)) {
            assertEquals(5, dictionary.size());
            assertEquals("fr", dictionary.language());
            // ordre de l'index : chat, Congrès, congres, congrès, plage
            assertEquals("chat", dictionary.read(0).title());
            assertEquals("Congrès", dictionary.read(1).title());
            assertEquals("congres", dictionary.read(2).title());
            assertEquals("congrès", dictionary.read(3).title());
            assertEquals("plage", dictionary.read(4).title());

            assertEquals(List.of("Pluriel de congre."), dictionary.find("congres").definitions().get("nom"));
            assertNull(dictionary.find("chien"));
            assertEquals(List.of("Congrès", "congres", "congrès"),
                    dictionary.search("CONGRES").stream().map(Entry::title).toList());
            assertEquals(List.of(), dictionary.search("ZZZ"));
        }
    }
}
