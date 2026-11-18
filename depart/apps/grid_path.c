#include <stdio.h>
#include <string.h>
#include "grid.h"
#include <stdlib.h>

int main(int argc, char* argv[]) {
    if (argc == 2 && strcmp("author", argv[1]) == 0) {
    printf("Auteur: Inaya Hamdaoui\n");
    return 0;
    }
    if (argc < 4) {
        printf("Erreur d'arguments.\n");
        return 255;
    }    

    Grille g;
    int result= lire_grille(&g, &argv[2], argc - 2 );
    if (result != 0) {
        printf("Erreur : grille incorrecte.\n");
        return 255;
    }
    //tests
        char* mot = argv[1];
    int nb_cases = g.hauteur * g.largeur;

    // une place par case de la grille
    int* visite = malloc(nb_cases * sizeof(int));
    int* chemin = malloc(nb_cases * sizeof(int));
    for (int i = 0; i < nb_cases; i++) {
        visite[i] = 0;     // au départ, aucune case n'est utilisée
    }

    // on essaie de commencer le mot sur chaque case, jusqu'à trouver
    int longueur = 0;
    for (int c = 0; c < nb_cases && longueur == 0; c++) {
        longueur = chercher_mot(&g, mot, c, visite, chemin, 0);
    }

    int code_retour = 1;           // 1 = mot absent
    if (longueur != 0) {
        for (int i = 0; i < longueur; i++) {
            printf("%d", chemin[i]);
            if (i < longueur - 1) {
                printf(" ");
            }
        }
        printf("\n");
        code_retour = 0;           // 0 = mot trouvé
    }

    free(visite);
    free(chemin);
    liberer_grille(&g);
    return code_retour;

}