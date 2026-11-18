#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "grid.h"
#include "dico.h"

/* Utilisation : solve dico.lex TAILLE_MIN HAUTEUR LARGEUR CASES...
 * Exemple     : solve dico.lex 3 4 4 G A I R R U V E QU E O T A S M J
 * Affiche tous les mots du dictionnaire présents dans la grille,
 * d'au moins TAILLE_MIN lettres, séparés par des espaces.
 * Valeurs de sortie :
 *   0   : tout va bien
 *   255 : arguments incorrects
 *   254 : fichier .lex introuvable ou incorrect
 *   253 : plus de mémoire
 *
 * Principe : c'est la même recherche que grid_path (on avance de voisine en
 * voisine, avec un tableau visite[]), mais au lieu de suivre UN mot donné,
 * on suit l'arbre du dictionnaire : à chaque case, on vérifie que les lettres
 * déjà lues forment le début d'un mot. Si ce n'est pas le cas, on arrête
 * cette piste tout de suite (c'est ce qui rend la recherche rapide).
 */

/* Toutes les informations de la recherche, regroupées pour ne pas passer
 * dix paramètres à chaque fonction. */
typedef struct {
    Grille* g;
    FILE* f;              /* le fichier .lex ouvert */
    Header h;             /* son en-tête */
    int taille_min;
    int* visite;          /* visite[i] = 1 si la case i est dans le chemin en cours */
    char* mot;            /* les lettres lues sur le chemin en cours */
    int longueur;         /* nombre de lettres dans mot */
    char** trouves;       /* les mots déjà trouvés (pour ne pas les afficher 2 fois) */
    int nb_trouves;
    int capacite;         /* nombre de places allouées dans trouves */
    int erreur;           /* 1 si un malloc a échoué */
} Recherche;

/* Renvoie 1 si le mot est déjà dans la liste des mots trouvés, 0 sinon. */
int deja_trouve(Recherche* r, char* mot) {
    for (int i = 0; i < r->nb_trouves; i++) {
        if (strcmp(r->trouves[i], mot) == 0) {
            return 1;
        }
    }
    return 0;
}

/* Ajoute une copie du mot à la liste des mots trouvés. */
void ajouter_trouve(Recherche* r, char* mot) {
    if (r->nb_trouves == r->capacite) {
        /* liste pleine : on double sa taille */
        int nouvelle_capacite = r->capacite * 2;
        char** nouveau = realloc(r->trouves, nouvelle_capacite * sizeof(char*));
        if (nouveau == NULL) {
            r->erreur = 1;
            return;
        }
        r->trouves = nouveau;
        r->capacite = nouvelle_capacite;
    }
    char* copie = malloc(strlen(mot) + 1);
    if (copie == NULL) {
        r->erreur = 1;
        return;
    }
    strcpy(copie, mot);
    r->trouves[r->nb_trouves] = copie;
    r->nb_trouves = r->nb_trouves + 1;
}

/* On arrive sur la case c. "num" est la cellule du dictionnaire où l'on se
 * trouve : la première des lettres qui peuvent venir maintenant. */
void explorer(Recherche* r, int c, int num) {
    char* lettres = r->g->cases[c].lettres;   /* "A" ou "QU" */
    int taille = strlen(lettres);

    /* 1. on avance dans le dictionnaire avec les lettres de la case */
    for (int i = 0; i < taille; i++) {
        num = findSibling(r->f, r->h, num, lettres[i]);
        if (num == NONE) {
            return;      /* aucun mot ne commence comme ça : on abandonne cette piste */
        }
        num = readCell(r->f, r->h, num).fils;
    }

    /* 2. on utilise la case : on la marque et on ajoute ses lettres au mot */
    r->visite[c] = 1;
    strcpy(r->mot + r->longueur, lettres);
    r->longueur = r->longueur + taille;

    /* 3. si un mot se termine ici ('\0' parmi les lettres possibles), on le garde */
    if (r->longueur >= r->taille_min && findSibling(r->f, r->h, num, '\0') != NONE) {
        if (!deja_trouve(r, r->mot)) {
            ajouter_trouve(r, r->mot);
        }
    }

    /* 4. on continue sur chaque voisine libre */
    int nb_cases = r->g->hauteur * r->g->largeur;
    for (int v = 0; v < nb_cases; v++) {
        if (voisines(r->g, c, v) && r->visite[v] == 0) {
            explorer(r, v, num);
        }
    }

    /* 5. on recule : on libère la case et on retire ses lettres du mot */
    r->visite[c] = 0;
    r->longueur = r->longueur - taille;
    r->mot[r->longueur] = '\0';
}

int main(int argc, char* argv[]) {
    if (argc == 2 && strcmp("author", argv[1]) == 0) {
        printf("Auteur: Inaya Hamdaoui\n");
        return 0;
    }
    if (argc < 5) {
        printf("Erreur d'arguments.\n");
        return 255;
    }

    int taille_min;
    if (sscanf(argv[2], "%d", &taille_min) != 1 || taille_min < 1) {
        printf("Erreur : taille minimum incorrecte.\n");
        return 255;
    }

    Grille g;
    if (lire_grille(&g, &argv[3], argc - 3) != 0) {
        printf("Erreur : grille incorrecte.\n");
        return 255;
    }

    FILE* f = fopen(argv[1], "rb");
    if (f == NULL) {
        printf("Erreur : fichier %s introuvable.\n", argv[1]);
        liberer_grille(&g);
        return 254;
    }
    Header h = readHeader(f);
    if (h.headerSize != sizeof(Header) || h.cellSize != sizeof(Cell)) {
        printf("Erreur : fichier %s incorrect.\n", argv[1]);
        fclose(f);
        liberer_grille(&g);
        return 254;
    }

    /* préparation de la recherche */
    int nb_cases = g.hauteur * g.largeur;
    Recherche r;
    r.g = &g;
    r.f = f;
    r.h = h;
    r.taille_min = taille_min;
    r.visite = malloc(nb_cases * sizeof(int));
    r.mot = malloc(2 * nb_cases + 1);         /* au pire, toutes les cases sont "QU" */
    r.longueur = 0;
    r.capacite = 100;
    r.trouves = malloc(r.capacite * sizeof(char*));
    r.nb_trouves = 0;
    r.erreur = 0;

    int code_retour = 0;
    if (r.visite == NULL || r.mot == NULL || r.trouves == NULL) {
        printf("Erreur : plus de memoire.\n");
        code_retour = 253;
    } else {
        for (int i = 0; i < nb_cases; i++) {
            r.visite[i] = 0;
        }
        r.mot[0] = '\0';

        /* on essaie de commencer un mot sur chaque case ;
         * dans le dictionnaire, on part de la racine (cellule 0) */
        if (h.cellCount > 0) {
            for (int c = 0; c < nb_cases; c++) {
                explorer(&r, c, 0);
            }
        }

        if (r.erreur) {
            printf("Erreur : plus de memoire.\n");
            code_retour = 253;
        } else {
            /* affichage des mots trouvés, séparés par des espaces */
            for (int i = 0; i < r.nb_trouves; i++) {
                printf("%s", r.trouves[i]);
                if (i < r.nb_trouves - 1) {
                    printf(" ");
                }
            }
            printf("\n");
        }
    }

    /* libération de toute la mémoire */
    if (r.trouves != NULL) {
        for (int i = 0; i < r.nb_trouves; i++) {
            free(r.trouves[i]);
        }
    }
    free(r.trouves);
    free(r.mot);
    free(r.visite);
    fclose(f);
    liberer_grille(&g);
    return code_retour;
}
