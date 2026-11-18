package fr.univ_eiffel.jdict;

import fr.univ_eiffel.jdict.text.LetterCounter;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LetterCounterTest {

    @Test
    void quIsDetectedAsADigram() {
        LetterCounter counter = new LetterCounter();
        for (int i = 0; i < 200; i++) {
            counter.count("quoi qui que quand bébé abri robe bus");
        }
        Map<String, Long> table = counter.table();
        assertTrue(table.containsKey("QU"));
        assertFalse(table.containsKey("Q"));
        assertTrue(table.containsKey("B"));   // B est suivi de lettres variées : pas de digramme
    }

    @Test
    void markupIsRemoved() {
        assertEquals("Étendue de sable.", LetterCounter.stripMarkup("{{lexique|géo|fr}} [[étendue|Étendue]] de [[sable]]."));
        assertEquals("Première de plager.", LetterCounter.stripMarkup("''Première de'' [[plager]]."));
    }
}
