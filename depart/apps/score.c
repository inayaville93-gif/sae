#include <string.h>
#include <stdio.h>
#include "boggle.h" 

int main(int argc, char* argv[]) {
    if (argc==2 && strcmp("author", argv[1])==0){
      printf("Auteur: Inaya Hamdaoui\n");
      return 0; 
    }
    char** mots = &argv[1];  //tous les paramètres passés sauf le premier
    int nombre = argc-1;     //le nombre de mots en paramètre
    printf("%d\n", score(mots, nombre));
    return 0;
}