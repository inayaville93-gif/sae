#include "boggle.h"
#include <stdlib.h>
#include <string.h>

int score_mot(char* mot){
    int longueur = strlen(mot);   
    if (longueur < 3) {
        return 0;
    } 
    if (longueur <= 4) {
        return 1;
    } 
    if (longueur <= 5) {
        return 2;
    } 
    if (longueur <= 6) {
        return 3;
    } 
    if (longueur <= 7) {
        return 5;
    }  
    
    return 11;

}

int score(char* mots[], int nombre){    
    int total = 0;
    for (int i = 0; i < nombre; i++) {
        total += score_mot(mots[i]);
    }
    return total;
}