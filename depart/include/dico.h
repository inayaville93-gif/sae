#pragma once
/* Librairie pour les dictionnaires : arbre lexicographique stocké dans un
 * tableau de cellules (version "fils / frère" vue en cours).
 *
 * Exemple avec les mots LA, LE, LES :
 *
 *   cellule 0 : racine (ne contient pas de lettre)
 *        |
 *        fils
 *        v
 *   cellule 1 : 'L'
 *        |
 *        fils
 *        v
 *   cellule 3 : 'E' (fin de mot : LE) --frère--> cellule 2 : 'A' (fin de mot : LA)
 *        |
 *        fils
 *        v
 *   cellule 4 : 'S' (fin de mot : LES)
 *
 * - "fils"  = la première lettre qui peut venir APRÈS celle-ci
 * - "frère" = une AUTRE lettre possible à la même position
 * - -1 veut dire "il n'y en a pas"
 */

/* En-tête du fichier .lex (imposé par l'énoncé : 4 champs) */
typedef struct {
    int taille_entete;    /* sizeof(Entete) */
    int nb_mots;          /* nombre de mots dans le dictionnaire */
    int nb_cellules;      /* nombre de cellules dans le tableau */
    int taille_cellule;   /* sizeof(Cellule) */
} Entete;

/* Une cellule de l'arbre (l'énoncé impose au moins 3 champs) */
typedef struct {
    char lettre;          /* la lettre de cette cellule */
    char fin_de_mot;      /* 1 si un mot se termine sur cette lettre, 0 sinon */
    int fils;             /* indice de la cellule fils, ou -1 */
    int frere;            /* indice de la cellule frère, ou -1 */
} Cellule;

/* Le dictionnaire en mémoire */
typedef struct {
    int nb_mots;
    int nb_cellules;      /* nombre de cellules utilisées */
    int capacite;         /* nombre de cellules allouées (>= nb_cellules) */
    Cellule* cellules;    /* tableau alloué avec malloc */
} Dico;

/* Crée un dictionnaire vide (avec seulement la racine).
 * Renvoie 0 si tout va bien, 1 si malloc a échoué. */
int creer_dico(Dico* d);

/* Libère la mémoire du dictionnaire. */
void liberer_dico(Dico* d);

/* Ajoute un mot (lettres A-Z uniquement) dans l'arbre.
 * Renvoie 0 si tout va bien, 1 si malloc a échoué. */
int ajouter_mot(Dico* d, char* mot);

/* Lit un dictionnaire texte (.txt, un mot par ligne) et remplit d.
 * Renvoie 0 si tout va bien, 1 si le fichier n'existe pas,
 * 2 si la mémoire manque. */
int lire_dico_txt(char* nom_fichier, Dico* d);

/* Écrit le dictionnaire dans un fichier binaire (.lex).
 * Renvoie 0 si tout va bien, 1 si le fichier ne peut pas être créé. */
int ecrire_dico_lex(char* nom_fichier, Dico* d);

/* Lit un fichier binaire (.lex) et remplit d.
 * Renvoie 0 si tout va bien, 1 si le fichier n'existe pas,
 * 2 si le fichier est incorrect ou si la mémoire manque. */
int lire_dico_lex(char* nom_fichier, Dico* d);

/* Cherche un mot dans le dictionnaire.
 * Renvoie 0 si le mot est présent,
 *         1 si c'est seulement le début (préfixe) d'un mot présent,
 *         2 sinon. */
int chercher_dico(Dico* d, char* mot);
