#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include "grid.h"

int lire_entier_positif(const char* s) {
    char* fin;
    long n = strtol(s, &fin, 10);
    /* fin doit pointer sur la fin de la chaîne : sinon il y avait autre chose
     * que des chiffres (ex: "4a") */
    if (*s == '\0' || *fin != '\0' || n <= 0 || n > 1000) {
        return -1;
    }
    return (int) n;
}

/* Vérifie qu'un symbole est composé de 1 ou 2 lettres majuscules A-Z */
static int symbole_valide(const char* s) {
    size_t len = strlen(s);
    if (len < 1 || len > 2) return 0;
    for (size_t i = 0; i < len; i++) {
        if (!isupper((unsigned char) s[i])) return 0;
    }
    return 1;
}

int lire_frequences(const char* nom_fichier, Frequences* freq) {
    FILE* fichier = fopen(nom_fichier, "r");
    if (fichier == NULL) {
        return -1;
    }

    freq->nb = 0;
    freq->total = 0;

    char ligne[256];
    while (fgets(ligne, sizeof ligne, fichier) != NULL) {
        char sym[16];
        int poids;
        int lus = sscanf(ligne, "%15s %d", sym, &poids);
        if (lus <= 0) continue;           /* ligne vide : on l'ignore */

        if (lus != 2 || poids < 0 || !symbole_valide(sym)
                || freq->nb >= MAX_SYMBOLES) {
            fclose(fichier);
            return -2;
        }
        strcpy(freq->symbole[freq->nb], sym);
        freq->poids[freq->nb] = poids;
        freq->total += poids;
        freq->nb++;
    }
    fclose(fichier);

    if (freq->total <= 0) {               /* fichier vide ou que des poids nuls */
        return -2;
    }
    return 0;
}

const char* tirer_symbole(const Frequences* freq) {
    /* On tire un nombre r dans [0, total[ puis on cherche dans quel
     * "intervalle" il tombe : symbole 0 couvre [0, p0[, symbole 1 couvre
     * [p0, p0+p1[, etc. Plus le poids est grand, plus l'intervalle est large. */
    long r = rand() % freq->total;
    for (int i = 0; i < freq->nb; i++) {
        if (r < freq->poids[i]) {
            return freq->symbole[i];
        }
        r -= freq->poids[i];
    }
    return freq->symbole[freq->nb - 1];   /* jamais atteint en théorie */
}

int creer_grille(Grille* g, int hauteur, int largeur) {
    g->hauteur = hauteur;
    g->largeur = largeur;
    g->cases = malloc((size_t) hauteur * largeur * sizeof *g->cases);
    return g->cases == NULL ? -1 : 0;
}

void liberer_grille(Grille* g) {
    free(g->cases);
    g->cases = NULL;
}

void remplir_grille(Grille* g, const Frequences* freq) {
    int n = g->hauteur * g->largeur;
    for (int i = 0; i < n; i++) {
        strcpy(g->cases[i], tirer_symbole(freq));
    }
}

void afficher_grille(const Grille* g) {
    int n = g->hauteur * g->largeur;
    for (int i = 0; i < n; i++) {
        printf("%s%s", g->cases[i], i < n - 1 ? " " : "\n");
    }
}

int grille_depuis_args(Grille* g, char* args[], int nb_args) {
    if (nb_args < 2) return -1;
    int hauteur = lire_entier_positif(args[0]);
    int largeur = lire_entier_positif(args[1]);
    if (hauteur < 0 || largeur < 0) return -1;
    if (nb_args - 2 != hauteur * largeur) return -1;   /* mauvais nombre de cases */

    if (creer_grille(g, hauteur, largeur) != 0) return -1;
    for (int i = 0; i < hauteur * largeur; i++) {
        if (!symbole_valide(args[2 + i])) {
            liberer_grille(g);
            return -1;
        }
        strcpy(g->cases[i], args[2 + i]);
    }
    return 0;
}
