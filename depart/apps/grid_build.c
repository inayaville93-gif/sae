#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <time.h>
#include <unistd.h>
#include "grid.h"

/* Usage : grid_build frequences.txt HAUTEUR LARGEUR
 * Codes de sortie :
 *   0   : OK
 *   255 : mauvais nombre d'arguments
 *   254 : dimensions incorrectes
 *   253 : fichier de fréquences introuvable
 *   252 : fichier de fréquences mal formé
 *   251 : erreur d'allocation mémoire
 */
int main(int argc, char* argv[]) {
    if (argc == 2 && strcmp("author", argv[1]) == 0) {
        printf("Auteur: Inaya Hamdaoui\n");
        return 0;
    }
    if (argc != 4) {
        fprintf(stderr, "Usage : %s frequences.txt hauteur largeur\n", argv[0]);
        return 255;
    }

    int hauteur = lire_entier_positif(argv[2]);
    int largeur = lire_entier_positif(argv[3]);
    if (hauteur < 0 || largeur < 0) {
        fprintf(stderr, "Erreur : dimensions incorrectes\n");
        return 254;
    }

    Frequences freq;
    int res = lire_frequences(argv[1], &freq);
    if (res == -1) {
        fprintf(stderr, "Erreur : impossible d'ouvrir %s\n", argv[1]);
        return 253;
    }
    if (res == -2) {
        fprintf(stderr, "Erreur : fichier de fréquences mal formé\n");
        return 252;
    }

    /* Graine aléatoire : l'heure seule ne suffit pas (deux appels dans la
     * même seconde donneraient la même grille), on mélange avec le pid. */
    srand((unsigned) time(NULL) ^ ((unsigned) getpid() << 16));

    Grille g;
    if (creer_grille(&g, hauteur, largeur) != 0) {
        fprintf(stderr, "Erreur : mémoire insuffisante\n");
        return 251;
    }
    remplir_grille(&g, &freq);
    afficher_grille(&g);
    liberer_grille(&g);
    return 0;
}
