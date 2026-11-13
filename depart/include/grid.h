#pragma once
/* Librairie pour les grilles de Boggle :
 *  - lecture d'un fichier de fréquences
 *  - tirage aléatoire d'une grille
 *  - lecture d'une grille passée en ligne de commande (pour grid_path, solve)
 */

#define TAILLE_CASE 3        /* une case contient 1 ou 2 lettres ("QU") + '\0' */
#define MAX_SYMBOLES 64      /* nombre max de lignes dans le fichier de fréquences */

/* Tableau de fréquences : symbole[i] apparaît avec le poids poids[i] */
typedef struct {
    int nb;                              /* nombre de symboles lus */
    char symbole[MAX_SYMBOLES][TAILLE_CASE];
    int poids[MAX_SYMBOLES];
    long total;                          /* somme des poids */
} Frequences;

/* Grille de hauteur x largeur cases, stockée ligne par ligne :
 * la case (ligne i, colonne j) est cases[i * largeur + j] */
typedef struct {
    int hauteur;
    int largeur;
    char (*cases)[TAILLE_CASE];
} Grille;

/* Lit le fichier de fréquences. Renvoie 0 si OK, -1 si le fichier est
 * introuvable, -2 si son contenu est incorrect. */
int lire_frequences(const char* nom_fichier, Frequences* freq);

/* Tire un symbole au hasard, proportionnellement à son poids. */
const char* tirer_symbole(const Frequences* freq);

/* Alloue une grille vide. Renvoie 0 si OK, -1 sinon. */
int creer_grille(Grille* g, int hauteur, int largeur);

/* Libère la mémoire d'une grille. */
void liberer_grille(Grille* g);

/* Remplit la grille aléatoirement selon les fréquences. */
void remplir_grille(Grille* g, const Frequences* freq);

/* Affiche la grille sur une ligne, cases séparées par des espaces. */
void afficher_grille(const Grille* g);

/* Construit une grille à partir des arguments de la ligne de commande
 * (ex: "4" "4" "G" "A" ... ). args pointe sur la hauteur, nb_args est le
 * nombre d'arguments disponibles à partir de là.
 * Renvoie 0 si OK, -1 si les arguments sont incorrects. */
int grille_depuis_args(Grille* g, char* args[], int nb_args);

/* Convertit une chaîne en entier strictement positif.
 * Renvoie l'entier, ou -1 si la chaîne n'est pas un entier > 0. */
int lire_entier_positif(const char* s);
