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

    FILE* f = fopen(argv[1], "rb");
    if (f == NULL) {
        printf("Erreur : fichier %s introuvable.\n", argv[1]);
        return 254;
    }
    
    Header h = readHeader(f);
    if (h.headerSize != sizeof(Header) || h.cellSize != sizeof(Cell)) {
        printf("Erreur : fichier %s incorrect.\n", argv[1]);
        fclose(f);
        return 254;
    }

    int resultat = lookupWord(f, h, argv[2]);
    fclose(f);
    return resultat;
}
