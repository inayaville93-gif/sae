#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "dico.h"

/* =====================================================================
 *  PARTIE 1 : construire l'arbre en mémoire (TP Arbres Lexicographiques)
 * ===================================================================== */

Trie newTrieNode(char elem) {
    Trie a = malloc(sizeof(TrieNode));
    if (a == NULL) {
        return NULL;
    }
    a->elem = elem;
    a->num = NONE;
    a->fils = NULL;
    a->frere = NULL;
    return a;
}

Trie lookupInsert(Trie* t, char x) {
    /* on parcourt t et ses frères */
    while (*t != NULL) {
        if ((*t)->elem == x) {
            return *t;            /* trouvé */
        }
        t = &((*t)->frere);       /* on passe au frère suivant */
    }
    /* pas trouvé : *t est le "frere" vide du dernier noeud (ou l'arbre vide),
     * on y accroche un nouveau noeud */
    *t = newTrieNode(x);
    return *t;
}

int insertString(Trie* t, char* s) {
    /* on descend lettre par lettre, '\0' compris (c'est la marque de fin de mot) */
    int i = 0;
    while (1) {
        Trie noeud = lookupInsert(t, s[i]);
        if (noeud == NULL) {
            return -1;
        }
        if (s[i] == '\0') {
            break;                /* on vient de traiter la fin du mot */
        }
        t = &(noeud->fils);       /* lettre suivante : on descend dans les fils */
        i++;
    }
    return 1;
}

void freeTrie(Trie t) {
    if (t == NULL) {
        return;
    }
    freeTrie(t->fils);
    freeTrie(t->frere);
    free(t);
}

/* Renvoie 1 si le mot ne contient que des lettres A-Z (et au moins une), 0 sinon. */
int motCorrect(char* mot) {
    if (mot[0] == '\0') {
        return 0;
    }
    for (int i = 0; mot[i] != '\0'; i++) {
        if (mot[i] < 'A' || mot[i] > 'Z') {
            return 0;
        }
    }
    return 1;
}

/* Renvoie 1 si le mot s est dans le Trie (utilisé pour ne pas compter
 * deux fois un mot en double dans le .txt), 0 sinon. */
int containsString(Trie t, char* s) {
    int i = 0;
    while (1) {
        while (t != NULL && t->elem != s[i]) {
            t = t->frere;
        }
        if (t == NULL) {
            return 0;
        }
        if (s[i] == '\0') {
            return 1;
        }
        t = t->fils;
        i++;
    }
}

int readDictionary(char* nom_fichier, Trie* t, int* nb_mots) {
    FILE* fichier = fopen(nom_fichier, "r");
    if (fichier == NULL) {
        return 1;
    }
    *t = NULL;
    *nb_mots = 0;

    char ligne[256];
    char mot[256];
    while (fgets(ligne, 256, fichier) != NULL) {
        /* l'énoncé : "une ligne commençant par un espace est ignorée" */
        if (ligne[0] == ' ') {
            continue;
        }
        /* on garde le premier mot de la ligne, le reste est un commentaire */
        if (sscanf(ligne, "%255s", mot) != 1) {
            continue;             /* ligne vide */
        }
        if (!motCorrect(mot)) {
            continue;             /* accents, minuscules... : ignoré */
        }
        if (containsString(*t, mot)) {
            continue;             /* mot déjà présent */
        }
        if (insertString(t, mot) == -1) {
            fclose(fichier);
            freeTrie(*t);
            return 2;
        }
        *nb_mots = *nb_mots + 1;
    }
    fclose(fichier);
    return 0;
}

/* =====================================================================
 *  PARTIE 2 : écrire l'arbre dans le fichier .lex (TP Arbres Statiques)
 * ===================================================================== */

int setNums(Trie t, int rootNumber) {
    if (t == NULL) {
        return 0;
    }
    /* parcours préfixe : d'abord le noeud, puis ses fils, puis ses frères */
    t->num = rootNumber;
    int taille_fils = setNums(t->fils, rootNumber + 1);
    int taille_freres = setNums(t->frere, rootNumber + 1 + taille_fils);
    return 1 + taille_fils + taille_freres;
}

/* Écrit les noeuds dans le fichier, dans le même ordre que setNums. */
void subtreeSave(Trie t, FILE* f) {
    if (t == NULL) {
        return;
    }
    Cell c;
    memset(&c, 0, sizeof(Cell));   /* met aussi à 0 les octets de "remplissage" */
    c.elem = t->elem;
    c.fils = NONE;
    c.frere = NONE;
    if (t->fils != NULL) {
        c.fils = t->fils->num;
    }
    if (t->frere != NULL) {
        c.frere = t->frere->num;
    }
    fwrite(&c, sizeof(Cell), 1, f);
    subtreeSave(t->fils, f);
    subtreeSave(t->frere, f);
}

int treeSave(Trie t, int nb_mots, char* nom_fichier) {
    FILE* f = fopen(nom_fichier, "wb");   /* "wb" = écriture en binaire */
    if (f == NULL) {
        return 0;
    }
    Header h;
    h.headerSize = sizeof(Header);
    h.wordCount = nb_mots;
    h.cellCount = setNums(t, 0);          /* la racine a le numéro 0 */
    h.cellSize = sizeof(Cell);

    fwrite(&h, sizeof(Header), 1, f);
    subtreeSave(t, f);
    fclose(f);
    return 1;
}

/* =====================================================================
 *  PARTIE 3 : lire le fichier .lex sans tout charger (TP Arbres Statiques)
 * ===================================================================== */

Header readHeader(FILE* f) {
    Header h;
    fseek(f, 0, SEEK_SET);                /* l'en-tête est au tout début */
    if (fread(&h, sizeof(Header), 1, f) != 1) {
        h.headerSize = 0;                 /* lecture ratée : en-tête invalide */
        h.wordCount = 0;
        h.cellCount = 0;
        h.cellSize = 0;
    }
    return h;
}

Cell readCell(FILE* f, Header h, int num) {
    Cell c;
    /* la cellule num se trouve après l'en-tête et les num cellules précédentes */
    long position = h.headerSize + (long) num * h.cellSize;
    fseek(f, position, SEEK_SET);
    if (fread(&c, sizeof(Cell), 1, f) != 1) {
        c.elem = '\0';
        c.fils = NONE;
        c.frere = NONE;
    }
    return c;
}

int findSibling(FILE* f, Header h, int num, char x) {
    while (num != NONE) {
        Cell c = readCell(f, h, num);
        if (c.elem == x) {
            return num;
        }
        num = c.frere;                    /* on passe au frère suivant */
    }
    return NONE;
}

int lookupWord(FILE* f, Header h, char* mot) {
    if (h.cellCount == 0) {
        return 2;                         /* dictionnaire vide */
    }
    int num = 0;                          /* on part de la racine */
    for (int i = 0; mot[i] != '\0'; i++) {
        num = findSibling(f, h, num, mot[i]);
        if (num == NONE) {
            return 2;                     /* la lettre n'existe pas à cet endroit */
        }
        num = readCell(f, h, num).fils;   /* on descend vers la lettre suivante */
    }
    /* on a suivi toutes les lettres : y a-t-il une fin de mot '\0' ici ? */
    if (findSibling(f, h, num, '\0') != NONE) {
        return 0;
    }
    return 1;
}
