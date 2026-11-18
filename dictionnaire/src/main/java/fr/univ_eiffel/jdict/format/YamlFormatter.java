package fr.univ_eiffel.jdict.format;

import fr.univ_eiffel.jdict.Entry;
import fr.univ_eiffel.jdict.Json;

import java.util.List;
import java.util.Map;

/**
 * Affiche l'entrée au format YAML, par exemple :
 * <pre>
 * - title: "congres"
 *   definitions:
 *     nom:
 *       - "''Pluriel de'' [[congre#fr|congre]]."
 * </pre>
 * Les textes sont écrits entre guillemets avec l'échappement JSON, qui est aussi du YAML valide.
 */
public class YamlFormatter implements EntryFormatter {
    @Override
    public String format(Entry entry) {
        StringBuilder yaml = new StringBuilder();
        yaml.append("- title: ").append(Json.write(entry.title())).append('\n');
        yaml.append("  definitions:");
        for (Map.Entry<String, List<String>> type : entry.definitions().entrySet()) {
            yaml.append('\n').append("    ").append(Json.write(type.getKey())).append(':');
            for (String definition : type.getValue()) {
                yaml.append('\n').append("      - ").append(Json.write(definition));
            }
        }
        return yaml.toString();
    }
}
