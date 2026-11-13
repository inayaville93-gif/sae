#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <time.h>
#include "grid.h"

/* Utilisation : grid_build frequences.txt hauteur largeur
 * Valeurs de sortie :
 *   0   : tout va bien
 *   255 : mauvais nombre d'arguments
 *   254 : hauteur ou largeur incorrecte
 *   253 : fichier de fréquences introuvable
 *   252 : fichier de fréquences mal écrit
 */
int main(int argc, char* argv[]) {
    if (argc == 2 && strcmp("author", argv[1]) == 0) {
        printf("Auteur: Inaya Hamdaoui\n");
        return 0;
    }
    if (argc != 4) {
        printf("Erreur d'arguments.\n");
        return 255;
    }

    Grille g;
    if (sscanf(argv[2], "%d", &g.hauteur) != 1 || sscanf(argv[3], "%d", &g.largeur) != 1) {
        printf("Erreur : la hauteur et la largeur doivent etre des nombres.\n");
        return 254;
    }
    if (g.hauteur <= 0 || g.largeur <= 0 || g.hauteur * g.largeur > MAX_CASES) {
        printf("Erreur : dimensions incorrectes.\n");
        return 254;
    }

    Frequences freq;
    int resultat = lire_frequences(argv[1], &freq);
    if (resultat == 1) {
        printf("Erreur : fichier %s introuvable.\n", argv[1]);
        return 253;
    }
    if (resultat == 2) {
        printf("Erreur : fichier de frequences mal ecrit.\n");
        return 252;
    }

    srand(time(NULL));   /* pour avoir une grille différente à chaque lancement */
    remplir_grille(&g, &freq);
    afficher_grille(&g);
    return 0;
}
