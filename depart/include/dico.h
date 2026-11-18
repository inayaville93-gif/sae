#pragma once
#include <stdio.h>
/* Librairie pour les dictionnaires, selon la méthode du cours
 * (TP "Arbres Child-Sibling / Arbres Lexicographiques" et TP "Arbres Statiques") :
 *
 *  1. on construit un arbre lexicographique AVEC DES POINTEURS (Trie),
 *     en version "fils / frère" ; un mot se termine par un noeud '\0'
 *  2. on numérote les noeuds (parcours préfixe, la racine a le numéro 0)
 *  3. on recopie les noeuds dans un fichier binaire .lex :
 *     un en-tête (Header) puis une cellule (Cell) par noeud
 *  4. en lecture, on ne charge PAS tout le fichier : on lit l'en-tête,
 *     puis seulement les cellules dont on a besoin (avec fseek)
 *
 * Exemple avec LA, LE, LES :
 *
 *   L
 *   |
 *   A ----> E
 *   |       |
 *   \0      \0 ---> S
 *                   |
 *                   \0
 *
 *   flèche vers le bas = fils (la lettre suivante du mot)
 *   flèche vers la droite = frère (une autre lettre possible au même endroit)
 */

#define NONE -1    /* "pas de fils" ou "pas de frère" */

/* ===== Arbre en mémoire (avec pointeurs) ===== */

typedef struct noeud {
    char elem;               /* la lettre, ou '\0' pour une fin de mot */
    int num;                 /* numéro du noeud dans le fichier .lex */
    struct noeud* fils;
    struct noeud* frere;
} TrieNode;

typedef TrieNode* Trie;

/* Crée un noeud contenant elem, sans fils ni frère (NULL si malloc échoue). */
Trie newTrieNode(char elem);

/* Cherche parmi t et ses frères le noeud contenant x ; le crée s'il n'existe pas.
 * Renvoie ce noeud (NULL si malloc échoue). */
Trie lookupInsert(Trie* t, char x);

/* Insère le mot s dans le Trie.
 * Renvoie 1 si le mot a été ajouté, 0 s'il y était déjà, -1 si malloc échoue. */
int insertString(Trie* t, char* s);

/* Libère tous les noeuds du Trie. */
void freeTrie(Trie t);

/* Lit un dictionnaire texte (.txt) et le range dans *t.
 * *nb_mots reçoit le nombre de mots différents.
 * Renvoie 0 si tout va bien, 1 si le fichier n'existe pas, 2 si la mémoire manque. */
int readDictionary(char* nom_fichier, Trie* t, int* nb_mots);

/* ===== Arbre sur disque (fichier .lex) ===== */

/* En-tête du fichier (imposé par l'énoncé : 4 champs) */
typedef struct {
    int headerSize;     /* sizeof(Header) */
    int wordCount;      /* nombre de mots */
    int cellCount;      /* nombre de cellules */
    int cellSize;       /* sizeof(Cell) */
} Header;

/* Une cellule = un noeud de l'arbre (l'énoncé impose au moins 3 champs) */
typedef struct {
    char elem;          /* la lettre, ou '\0' */
    int fils;           /* numéro de la cellule fils, ou NONE */
    int frere;          /* numéro de la cellule frère, ou NONE */
} Cell;

/* Numérote les noeuds en ordre préfixe à partir de rootNumber.
 * Renvoie le nombre de noeuds. */
int setNums(Trie t, int rootNumber);

/* Recopie l'arbre dans le fichier .lex.
 * Renvoie 1 si tout va bien, 0 si le fichier ne peut pas être créé. */
int treeSave(Trie t, int nb_mots, char* nom_fichier);

/* Lit l'en-tête d'un fichier .lex ouvert. */
Header readHeader(FILE* f);

/* Lit la cellule numéro num, sans lire le reste du fichier (avec fseek). */
Cell readCell(FILE* f, Header h, int num);

/* Parmi la cellule num et ses frères, cherche celle qui contient x.
 * Renvoie son numéro, ou NONE si aucune ne contient x. */
int findSibling(FILE* f, Header h, int num, char x);

/* Cherche un mot dans le fichier .lex.
 * Renvoie 0 si le mot est présent,
 *         1 si c'est seulement le début (préfixe) d'un mot présent,
 *         2 sinon. */
int lookupWord(FILE* f, Header h, char* mot);
