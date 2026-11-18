#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "dico.h"

int creer_dico(Dico* d) {
    d->nb_mots = 0;
    d->capacite = 1000;   /* on commence avec 1000 places, on agrandira si besoin */
    d->cellules = malloc(d->capacite * sizeof(Cellule));
    if (d->cellules == NULL) {
        return 1;
    }
    /* la cellule 0 est la racine : elle ne contient pas de lettre.
     * memset met tous les octets à 0 (y compris les octets "de remplissage"
     * que le compilateur ajoute dans la structure), pour écrire un fichier propre */
    memset(&d->cellules[0], 0, sizeof(Cellule));
    d->cellules[0].lettre = '\0';
    d->cellules[0].fin_de_mot = 0;
    d->cellules[0].fils = -1;
    d->cellules[0].frere = -1;
    d->nb_cellules = 1;
    return 0;
}

void liberer_dico(Dico* d) {
    free(d->cellules);
}

/* Ajoute une nouvelle cellule à la fin du tableau et renvoie son indice
 * (ou -1 si la mémoire manque). Si le tableau est plein, on double sa taille
 * avec realloc. */
int nouvelle_cellule(Dico* d, char lettre) {
    if (d->nb_cellules == d->capacite) {
        int nouvelle_capacite = d->capacite * 2;
        Cellule* nouveau = realloc(d->cellules, nouvelle_capacite * sizeof(Cellule));
        if (nouveau == NULL) {
            return -1;
        }
        d->cellules = nouveau;
        d->capacite = nouvelle_capacite;
    }
    int i = d->nb_cellules;
    memset(&d->cellules[i], 0, sizeof(Cellule));
    d->cellules[i].lettre = lettre;
    d->cellules[i].fin_de_mot = 0;
    d->cellules[i].fils = -1;
    d->cellules[i].frere = -1;
    d->nb_cellules = d->nb_cellules + 1;
    return i;
}

/* Cherche, parmi les fils de la cellule "parent", celui qui contient "lettre".
 * Renvoie son indice, ou -1 s'il n'existe pas. */
int trouver_fils(Dico* d, int parent, char lettre) {
    int c = d->cellules[parent].fils;     /* premier fils */
    while (c != -1) {
        if (d->cellules[c].lettre == lettre) {
            return c;
        }
        c = d->cellules[c].frere;          /* on passe au frère suivant */
    }
    return -1;
}

int ajouter_mot(Dico* d, char* mot) {
    int courant = 0;    /* on part de la racine */
    for (int i = 0; mot[i] != '\0'; i++) {
        int suivant = trouver_fils(d, courant, mot[i]);
        if (suivant == -1) {
            /* la lettre n'existe pas encore à cet endroit : on la crée */
            suivant = nouvelle_cellule(d, mot[i]);
            if (suivant == -1) {
                return 1;
            }
            /* on la place en tête de la liste des fils de "courant" :
             * son frère devient l'ancien premier fils */
            d->cellules[suivant].frere = d->cellules[courant].fils;
            d->cellules[courant].fils = suivant;
        }
        courant = suivant;   /* on descend d'un niveau */
    }
    /* on est sur la dernière lettre : on marque la fin du mot
     * (sauf s'il était déjà dans le dictionnaire, pour ne pas le compter 2 fois) */
    if (d->cellules[courant].fin_de_mot == 0) {
        d->cellules[courant].fin_de_mot = 1;
        d->nb_mots = d->nb_mots + 1;
    }
    return 0;
}

/* Renvoie 1 si le mot ne contient que des lettres A-Z (et au moins une), 0 sinon. */
int mot_correct(char* mot) {
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

int lire_dico_txt(char* nom_fichier, Dico* d) {
    FILE* fichier = fopen(nom_fichier, "r");
    if (fichier == NULL) {
        return 1;
    }
    if (creer_dico(d) != 0) {
        fclose(fichier);
        return 2;
    }

    char ligne[256];
    char mot[256];
    while (fgets(ligne, 256, fichier) != NULL) {
        /* l'énoncé : "une ligne commençant par un espace est ignorée" */
        if (ligne[0] == ' ') {
            continue;
        }
        /* on garde seulement le premier mot de la ligne (le reste = commentaire) */
        if (sscanf(ligne, "%255s", mot) != 1) {
            continue;   /* ligne vide */
        }
        if (!mot_correct(mot)) {
            continue;   /* mot avec accent, minuscule... : on l'ignore */
        }
        if (ajouter_mot(d, mot) != 0) {
            fclose(fichier);
            liberer_dico(d);
            return 2;
        }
    }
    fclose(fichier);
    return 0;
}

int ecrire_dico_lex(char* nom_fichier, Dico* d) {
    FILE* fichier = fopen(nom_fichier, "wb");   /* "wb" = écriture en binaire */
    if (fichier == NULL) {
        return 1;
    }
    Entete e;
    e.taille_entete = sizeof(Entete);
    e.nb_mots = d->nb_mots;
    e.nb_cellules = d->nb_cellules;
    e.taille_cellule = sizeof(Cellule);

    /* fwrite(adresse, taille d'un élément, nombre d'éléments, fichier) */
    fwrite(&e, sizeof(Entete), 1, fichier);
    fwrite(d->cellules, sizeof(Cellule), d->nb_cellules, fichier);
    fclose(fichier);
    return 0;
}

int lire_dico_lex(char* nom_fichier, Dico* d) {
    FILE* fichier = fopen(nom_fichier, "rb");   /* "rb" = lecture en binaire */
    if (fichier == NULL) {
        return 1;
    }
    Entete e;
    if (fread(&e, sizeof(Entete), 1, fichier) != 1) {
        fclose(fichier);
        return 2;
    }
    /* on vérifie que le fichier a bien été créé avec les mêmes structures */
    if (e.taille_entete != sizeof(Entete) || e.taille_cellule != sizeof(Cellule)
            || e.nb_cellules < 1) {
        fclose(fichier);
        return 2;
    }
    d->nb_mots = e.nb_mots;
    d->nb_cellules = e.nb_cellules;
    d->capacite = e.nb_cellules;
    d->cellules = malloc(e.nb_cellules * sizeof(Cellule));
    if (d->cellules == NULL) {
        fclose(fichier);
        return 2;
    }
    if ((int) fread(d->cellules, sizeof(Cellule), e.nb_cellules, fichier) != e.nb_cellules) {
        fclose(fichier);
        free(d->cellules);
        return 2;
    }
    fclose(fichier);
    return 0;
}

int chercher_dico(Dico* d, char* mot) {
    int courant = 0;    /* on part de la racine */
    for (int i = 0; mot[i] != '\0'; i++) {
        courant = trouver_fils(d, courant, mot[i]);
        if (courant == -1) {
            return 2;   /* la lettre n'existe pas à cet endroit : ni mot, ni préfixe */
        }
    }
    if (d->cellules[courant].fin_de_mot == 1) {
        return 0;       /* un mot se termine ici */
    }
    return 1;           /* on a pu suivre toutes les lettres, mais aucun mot ne finit ici */
}
