package fr.univ_eiffel.jdict;

import fr.univ_eiffel.jdict.lang.Language;
import fr.univ_eiffel.jdict.lang.Languages;
import fr.univ_eiffel.jdict.storage.DictionaryLocation;
import fr.univ_eiffel.jdict.storage.DictionaryWriter;
import fr.univ_eiffel.jdict.wiki.DefinitionExtractor;
import fr.univ_eiffel.jdict.wiki.DumpReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

/**
 * Crée le fichier de définitions et son index à partir d'un dump lu sur l'entrée standard.
 * Usage : cat frwiktionary.xml.bz2 | java fr.univ_eiffel.jdict.DictionaryMaker fr definitions
 *         (ou "compressed:definitions" pour compresser les entrées avec zlib)
 */
public class DictionaryMaker {

    public static void main(String[] args) {
        Cli.run(() -> run(args));
    }

    private static void run(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage : DictionaryMaker <langue> <[compressed:]fichier>");
            System.err.println("Langues disponibles : " + Languages.codes());
            System.exit(1);
        }
        Language language = Languages.get(args[0]);
        DictionaryLocation location = DictionaryLocation.parse(args[1]);
        DefinitionExtractor extractor = new DefinitionExtractor(language);

        try (InputStream xml = DumpReader.decompressIfNeeded(System.in);
             DictionaryWriter writer = new DictionaryWriter(location, language.code())) {
            new DumpReader().read(xml, page -> {
                Entry entry = extractor.extract(page);
                if (entry != null) {
                    try {
                        writer.add(entry);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                    if (writer.size() % 100_000 == 0) {
                        System.err.println(writer.size() + " mots...");
                    }
                }
            });
            System.err.println(writer.size() + " mots écrits dans " + location.dictFile()
                    + " et " + location.indexFile());
        }
    }
}
