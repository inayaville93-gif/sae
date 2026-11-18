#include <stdio.h>
#include <string.h>
#include "dico.h"

/* Utilisation : dictionnary_build dico.txt dico.lex
 * Valeurs de sortie :
 *   0   : tout va bien
 *   255 : mauvais nombre d'arguments
 *   254 : fichier .txt introuvable
 *   253 : impossible de créer le fichier .lex
 *   252 : plus de mémoire
 */
int main(int argc, char* argv[]) {
    if (argc == 2 && strcmp("author", argv[1]) == 0) {
        printf("Auteur: Inaya Hamdaoui\n");
        return 0;
    }
    if (argc != 3) {
        printf("Erreur d'arguments.\n");
        return 255;
    }

    /* 1. lire le .txt et construire l'arbre en mémoire */
    Trie t;
    int nb_mots;
    int resultat = readDictionary(argv[1], &t, &nb_mots);
    if (resultat == 1) {
        printf("Erreur : fichier %s introuvable.\n", argv[1]);
        return 254;
    }
    if (resultat == 2) {
        printf("Erreur : plus de memoire.\n");
        return 252;
    }

    /* 2. recopier l'arbre dans le .lex */
    if (!treeSave(t, nb_mots, argv[2])) {
        printf("Erreur : impossible de creer %s.\n", argv[2]);
        freeTrie(t);
        return 253;
    }

    freeTrie(t);
    return 0;
}
