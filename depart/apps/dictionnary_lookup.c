#include <stdio.h>
#include <string.h>
#include "dico.h"

/* Utilisation : dictionnary_lookup dico.lex MOT
 * Valeurs de sortie :
 *   0   : le mot est dans le dictionnaire
 *   1   : le mot est le début (préfixe) d'un mot du dictionnaire
 *   2   : ni l'un ni l'autre
 *   255 : mauvais nombre d'arguments
 *   254 : fichier .lex introuvable ou incorrect
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

    Dico d;
    if (lire_dico_lex(argv[1], &d) != 0) {
        printf("Erreur : fichier %s introuvable ou incorrect.\n", argv[1]);
        return 254;
    }

    int resultat = chercher_dico(&d, argv[2]);
    liberer_dico(&d);
    return resultat;
}
