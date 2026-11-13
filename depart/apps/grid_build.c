#include <stdio.h>
#include <string.h>
#include <stdlib.h>

void lire_frequences(char* f) {
    FILE *fichier = fopen(f,"r");
    if (f == NULL) {
        printf("")
    }
}
int main(int argc, char* argv[]){
    if (argc==2 && strcmp("author", argv[1])==0){
        printf("Auteur: Inaya Hamdaoui\n");
        return 255; 
    }
    if (argc !=4){
        printf("Erreur d'arguments.");
        return 254;
    }

    int hauteur;
    int largeur;
    sscanf(argv[2],"%d", &hauteur);
    sscanf(argv[3],"%d", &largeur);

    printf("Les dimensions sont : %ls lignes, %ls colonnes", &hauteur, &largeur);

    return 0;

}