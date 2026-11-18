#include <stdio.h>
#include <string.h>
#include "dico.h"

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

    int result = chercher_dico(&d, argv[2]);
    liberer_dico(&d);
    return result;
}
