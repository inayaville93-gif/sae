package fr.univ_eiffel.jdict;

/** Lancement des programmes en ligne de commande avec un message d'erreur lisible. */
public final class Cli {

    /** Le corps d'un programme, qui peut lever n'importe quelle exception. */
    @FunctionalInterface
    public interface Program {
        void run() throws Exception;
    }

    private Cli() {
    }

    /** Exécute le programme ; en cas d'erreur, affiche un message clair et quitte avec le code 3. */
    public static void run(Program program) {
        try {
            program.run();
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.toString();
            System.err.println("Erreur : " + message);
            System.exit(3);
        }
    }
}
