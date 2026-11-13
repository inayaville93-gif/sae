#pragma once
/* Librairie pour les grilles de Boggle */

#define MAX_SYMBOLES 50    /* nombre max de lignes dans le fichier de fréquences */
#define MAX_CASES 400      /* nombre max de cases dans une grille (ex: 20 x 20) */

/* Le contenu du fichier de fréquences :
 * la lettre symbole[i] a le poids poids[i] */
typedef struct {
    int nb;                       /* nombre de lettres lues */
    char symbole[MAX_SYMBOLES][3]; /* "A", "B", ..., "QU" (2 lettres max + '\0') */
    int poids[MAX_SYMBOLES];
    int total;                    /* somme de tous les poids */
} Frequences;

/* Une grille : les cases sont rangées ligne par ligne,
 * la case (ligne i, colonne j) est cases[i * largeur + j] */
typedef struct {
    int hauteur;
    int largeur;
    char cases[MAX_CASES][3];
} Grille;

/* Lit le fichier de fréquences.
 * Renvoie 0 si tout va bien, 1 si le fichier n'existe pas,
 * 2 si le fichier est mal écrit. */
int lire_frequences(char* nom_fichier, Frequences* freq);

/* Tire une lettre au hasard en respectant les poids. */
char* tirer_symbole(Frequences* freq);

/* Remplit toutes les cases de la grille au hasard. */
void remplir_grille(Grille* g, Frequences* freq);

/* Affiche la grille sur une seule ligne, cases séparées par des espaces. */
void afficher_grille(Grille* g);

/* Lit une grille donnée en ligne de commande, par exemple : 4 4 G A I R ...
 * args[0] est la hauteur, args[1] la largeur, puis viennent les cases.
 * nb_args est le nombre d'éléments dans args.
 * Renvoie 0 si tout va bien, 1 si les arguments sont incorrects. */
int lire_grille(Grille* g, char* args[], int nb_args);
