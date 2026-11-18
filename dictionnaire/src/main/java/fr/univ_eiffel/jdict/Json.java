package fr.univ_eiffel.jdict;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.UncheckedIOException;
import java.util.Map;

/** Conversion entre objets Java et texte JSON (avec la bibliothèque Jackson). */
public final class Json {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Json() {
    }

    /** Transforme un objet (record, Map, String...) en une ligne JSON. */
    public static String write(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Relit une ligne JSON représentant une entrée du dictionnaire. */
    public static Entry readEntry(String json) {
        try {
            return MAPPER.readValue(json, Entry.class);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Relit une ligne JSON représentant un objet simple (ex : l'en-tête du fichier). */
    public static Map<String, Object> readMap(String json) {
        try {
            return MAPPER.readValue(json, new TypeReference<Map<String, Object>>() { });
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }
}
