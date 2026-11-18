package fr.univ_eiffel.jdict;

import fr.univ_eiffel.jdict.lang.Languages;
import fr.univ_eiffel.jdict.wiki.DefinitionExtractor;
import fr.univ_eiffel.jdict.wiki.WikiPage;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DefinitionExtractorTest {

    private static final String PLAGE = """
            == {{langue|fr}} ==
            === {{S|étymologie}} ===
            : Du latin.
            === {{S|nom|fr}} ===
            # Étendue de [[sable]].
            #* ''Un exemple.''
            #: Une note.
            # Partie d’un tout.
            ==== {{S|synonymes}} ====
            * [[grève]]
            === {{S|verbe|fr|flexion}} ===
            # ''Première personne de'' [[plager]].
            == {{langue|de}} ==
            === {{S|verbe|de|flexion}} ===
            # Allemand, à ignorer.
            """;

    @Test
    void frenchDefinitionsOnly() {
        Entry entry = new DefinitionExtractor(Languages.get("fr")).extract(new WikiPage("plage", 0, PLAGE));
        assertEquals("plage", entry.title());
        assertEquals(List.of("nom", "verbe"), List.copyOf(entry.definitions().keySet()));
        assertEquals(List.of("Étendue de [[sable]].", "Partie d’un tout."), entry.definitions().get("nom"));
        assertEquals(List.of("''Première personne de'' [[plager]]."), entry.definitions().get("verbe"));
    }

    @Test
    void pageWithoutFrenchSectionGivesNothing() {
        String text = "== {{langue|de}} ==\n=== {{S|nom|de}} ===\n# Chien.\n";
        assertNull(new DefinitionExtractor(Languages.get("fr")).extract(new WikiPage("Hund", 0, text)));
    }

    @Test
    void englishSectionsUnderEtymology() {
        String text = """
                ==English==
                ===Etymology 1===
                ====Verb====
                # To guide.
                ===Etymology 2===
                ====Noun====
                # A metal.
                ==French==
                ===Noun===
                # ignored
                """;
        Entry entry = new DefinitionExtractor(Languages.get("en")).extract(new WikiPage("lead", 0, text));
        assertEquals(Set.of("verb", "noun"), entry.definitions().keySet());
        assertEquals(List.of("A metal."), entry.definitions().get("noun"));
    }
}
