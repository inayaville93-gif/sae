package fr.univ_eiffel.jdict;

import fr.univ_eiffel.jdict.storage.Dictionary;
import fr.univ_eiffel.jdict.storage.DictionaryLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Interface interactive : on ouvre le dictionnaire une seule fois, puis on peut
 * poser plusieurs questions (on évite de relancer la JVM à chaque mot).
 * Usage : java fr.univ_eiffel.jdict.InteractiveUI [definitions]
 */
public class InteractiveUI {
    private Dictionary dictionary;

    public static void main(String[] args) throws IOException {
        InteractiveUI ui = new InteractiveUI();
        if (args.length >= 1) {
            ui.open(args[0]);
        }
        ui.run();
    }

    private void open(String name) {
        try {
            Dictionary opened = new Dictionary(DictionaryLocation.parse(name));
            close();
            dictionary = opened;
            Output.OUT.println("Dictionnaire " + name + " ouvert (" + dictionary.size() + " mots, langue "
                    + dictionary.language() + ").");
        } catch (IOException | RuntimeException e) {
            Output.OUT.println("Impossible d'ouvrir " + name + " : " + e.getMessage());
        }
    }

    private void close() throws IOException {
        if (dictionary != null) {
            dictionary.close();
            dictionary = null;
        }
    }

    private static void help() {
        Output.OUT.println("""
                Commandes :
                  mot            affiche les définitions du mot (JSON)
                  MOT            affiche tous les mots de forme normalisée MOT
                  yaml:mot       même chose, au format YAML
                  :open fichier  ouvre un autre dictionnaire
                  :info          informations sur le dictionnaire ouvert
                  :help          affiche cette aide
                  :quit          quitte""");
    }

    private void run() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        help();
        while (true) {
            Output.OUT.print("> ");
            Output.OUT.flush();
            String line = in.readLine();
            if (line == null) {
                break;                                  // fin de l'entrée (Ctrl+D)
            }
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.equals(":quit") || line.equals(":exit")) {
                break;
            } else if (line.equals(":help")) {
                help();
            } else if (line.startsWith(":open ")) {
                open(line.substring(":open ".length()).trim());
            } else if (line.equals(":info")) {
                if (dictionary == null) {
                    Output.OUT.println("Aucun dictionnaire ouvert.");
                } else {
                    Output.OUT.println(dictionary.size() + " mots, langue " + dictionary.language());
                }
            } else if (line.startsWith(":")) {
                Output.OUT.println("Commande inconnue, tapez :help");
            } else {
                search(line);
            }
        }
        close();
    }

    private void search(String text) {
        if (dictionary == null) {
            Output.OUT.println("Aucun dictionnaire ouvert : utilisez :open fichier");
            return;
        }
        SearchRequest request = SearchRequest.parse(text);
        try {
            List<Entry> entries = dictionary.search(request.word());
            if (entries.isEmpty()) {
                Output.OUT.println("Mot introuvable : " + request.word());
            }
            for (Entry entry : entries) {
                Output.OUT.println(request.formatter().format(entry));
            }
        } catch (IOException | RuntimeException e) {
            Output.OUT.println("Erreur de lecture : " + e.getMessage());
        }
    }
}
