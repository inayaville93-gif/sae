#include "boggle.h"

int score(char* mots[], int nombre){    
    //implementation par defaut du score = nombre de mots, sans regarder le contenu
    // Supprime le warning pour 'nb_mots' s'il n'est pas utilisé
    (void)mots; 
    return nombre;
}