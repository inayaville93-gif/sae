package fr.univ_eiffel.jdict;

import fr.univ_eiffel.jdict.text.WordNormalizer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WordNormalizerTest {

    @Test
    void normalizeRemovesAccentsAndUppercases() {
        assertEquals("CONGRES", WordNormalizer.normalize("congrès"));
        assertEquals("CONGRES", WordNormalizer.normalize("Congrès"));
        assertEquals("COEUR", WordNormalizer.normalize("cœur"));
        assertEquals("GARCON", WordNormalizer.normalize("garçon"));
    }

    @Test
    void orderFollowsTheSubject() {
        List<String> words = new ArrayList<>(List.of("congrès", "chat", "congres", "Congrès", "plage"));
        words.sort(WordNormalizer.ORDER);
        assertEquals(List.of("chat", "Congrès", "congres", "congrès", "plage"), words);
    }

    @Test
    void uppercaseWordsAreNormalized() {
        assertTrue(WordNormalizer.isNormalized("CONGRES"));
        assertFalse(WordNormalizer.isNormalized("congres"));
        assertFalse(WordNormalizer.isNormalized("CONGRÈS"));
    }
}
