package fr.univ_eiffel.jdict;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Sortie standard en UTF-8, quelle que soit la configuration de la machine
 * (sinon les accents peuvent s'afficher en "?").
 */
public final class Output {
    public static final PrintStream OUT =
            new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8);

    private Output() {
    }
}
