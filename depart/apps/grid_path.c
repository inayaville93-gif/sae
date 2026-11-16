#include <stdio.h>
#include <string.h>
#include "grid.h"

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
    char* mot = argv[1];
    printf("Mot : %s\n", mot);   
    afficher_grille(&g);        
    liberer_grille(&g);         
    return 0;

}