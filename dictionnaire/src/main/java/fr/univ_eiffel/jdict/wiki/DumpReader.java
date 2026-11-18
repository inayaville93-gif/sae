package fr.univ_eiffel.jdict.wiki;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

/**
 * Lecture d'un dump Wiktionary avec l'API StAX (lecture "pull" : on demande les
 * événements XML un par un, sans charger tout le fichier en mémoire).
 */
public class DumpReader {

    /**
     * Si le flux est compressé en bzip2 (il commence par "BZh"), on le décompresse ;
     * sinon on le considère comme du XML brut (pratique pour les tests).
     */
    public static InputStream decompressIfNeeded(InputStream in) throws IOException {
        BufferedInputStream buffered = new BufferedInputStream(in, 1 << 16);
        buffered.mark(3);
        byte[] magic = buffered.readNBytes(3);
        buffered.reset();
        if (magic.length == 3 && magic[0] == 'B' && magic[1] == 'Z' && magic[2] == 'h') {
            // true : le dump peut être constitué de plusieurs blocs bzip2 à la suite
            return new BufferedInputStream(new BZip2CompressorInputStream(buffered, true), 1 << 16);
        }
        return buffered;
    }

    /**
     * Lit toutes les pages du flux XML et appelle action.accept(page) pour chaque page
     * de l'espace de noms 0 (les vrais articles du dictionnaire).
     */
    public void read(InputStream xml, Consumer<WikiPage> action) throws XMLStreamException {
        // les dumps sont énormes : on lève les limites de taille de l'analyseur XML du JDK
        System.setProperty("jdk.xml.totalEntitySizeLimit", "0");
        System.setProperty("jdk.xml.maxGeneralEntitySizeLimit", "0");
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_COALESCING, true);
        XMLStreamReader reader = factory.createXMLStreamReader(xml, "UTF-8");

        String title = null;
        int namespace = -1;
        String text = null;
        try {
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    switch (reader.getLocalName()) {
                        case "page" -> {          // nouvelle page : on oublie la précédente
                            title = null;
                            namespace = -1;
                            text = null;
                        }
                        case "title" -> title = reader.getElementText();
                        case "ns" -> namespace = Integer.parseInt(reader.getElementText().trim());
                        case "text" -> text = reader.getElementText();
                        default -> { }
                    }
                } else if (event == XMLStreamConstants.END_ELEMENT && reader.getLocalName().equals("page")) {
                    if (namespace == 0 && title != null && text != null) {
                        action.accept(new WikiPage(title, namespace, text));
                    }
                }
            }
        } finally {
            reader.close();
        }
    }
}
