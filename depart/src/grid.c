#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "grid.h"

/* Renvoie 1 si le mot contient 1 ou 2 lettres majuscules (ex: "A", "QU"),
 * 0 sinon. */
int symbole_correct(char* mot) {
    int longueur = strlen(mot);
    if (longueur < 1 || longueur > 2) {
        return 0;
    }
    for (int i = 0; i < longueur; i++) {
        if (mot[i] < 'A' || mot[i] > 'Z') {
            return 0;
        }
    }
    return 1;
}

int lire_frequences(char* nom_fichier, Frequences* freq) {
    FILE* fichier = fopen(nom_fichier, "r");
    if (fichier == NULL) {
        return 1;
    }

    freq->nb = 0;
    freq->total = 0;

    char lettre[10];
    int poids;
    /* fscanf renvoie le nombre de valeurs lues : 2 si on a lu "A 36" */
    while (fscanf(fichier, "%9s %d", lettre, &poids) == 2) {
        if (!symbole_correct(lettre) || poids < 0 || freq->nb >= MAX_SYMBOLES) {
            fclose(fichier);
            return 2;
        }
        strcpy(freq->symbole[freq->nb], lettre);
        freq->poids[freq->nb] = poids;
        freq->total = freq->total + poids;
        freq->nb = freq->nb + 1;
    }
    fclose(fichier);

    if (freq->total == 0) {   /* fichier vide */
        return 2;
    }
    return 0;
}

char* tirer_symbole(Frequences* freq) {
    /* On tire un nombre entre 0 et total-1.
     * Exemple avec A 36, B 5, C 15 (total = 56) :
     *   de 0 à 35  -> A
     *   de 36 à 40 -> B
     *   de 41 à 55 -> C
     * Plus le poids est grand, plus la lettre a de chances de sortir. */
    int r = rand() % freq->total;
    for (int i = 0; i < freq->nb; i++) {
        if (r < freq->poids[i]) {
            return freq->symbole[i];
        }
        r = r - freq->poids[i];
    }
    return freq->symbole[freq->nb - 1];
}

int creer_grille(Grille* g, int hauteur, int largeur) {
    g->hauteur = hauteur;
    g->largeur = largeur;
    g->cases = malloc(hauteur * largeur * sizeof(Case));
    if (g->cases == NULL) {
        return 1;
    }
    return 0;
}

void liberer_grille(Grille* g) {
    free(g->cases);
}

void remplir_grille(Grille* g, Frequences* freq) {
    int nb_cases = g->hauteur * g->largeur;
    for (int i = 0; i < nb_cases; i++) {
        strcpy(g->cases[i].lettres, tirer_symbole(freq));
    }
}

void afficher_grille(Grille* g) {
    int nb_cases = g->hauteur * g->largeur;
    for (int i = 0; i < nb_cases; i++) {
        printf("%s", g->cases[i].lettres);
        if (i < nb_cases - 1) {
            printf(" ");
        }
    }
    printf("\n");
}

int lire_grille(Grille* g, char* args[], int nb_args) {
    if (nb_args < 2) {
        return 1;
    }
    int hauteur, largeur;
    if (sscanf(args[0], "%d", &hauteur) != 1 || sscanf(args[1], "%d", &largeur) != 1) {
        return 1;
    }
    if (hauteur <= 0 || largeur <= 0) {
        return 1;
    }
    int nb_cases = hauteur * largeur;
    if (nb_args - 2 != nb_cases) {   /* il doit y avoir exactement hauteur*largeur cases */
        return 1;
    }
    if (creer_grille(g, hauteur, largeur) != 0) {
        return 1;
    }
    for (int i = 0; i < nb_cases; i++) {
        if (!symbole_correct(args[2 + i])) {
            liberer_grille(g);
            return 1;
        }
        strcpy(g->cases[i].lettres, args[2 + i]);
    }
    return 0;
}
