INSERT INTO utilisateur (pseudo, mdp_hash, adresse_mail)
VALUES ('pierre', 'cestlhistoire', 'pierre@mail.com');

INSERT INTO utilisateur (pseudo, mdp_hash, adresse_mail)
VALUES ('lou', 'depierreetleloup', 'lou@mail.com');

INSERT INTO utilisateur (pseudo, mdp_hash, adresse_mail)
VALUES ('jean', 'unjourildit', 'jean@mail.com');

INSERT INTO Partie (donnees) VALUES ('{
  "grille": {
    "hauteur": 4,
    "largeur": 4,
    "cases": ["G","A","I","R","R","U","V","E","QU","E","O","T","A","S","M","J"]
  },
  "parametres": { "langue": "fr", "taille_min": 3, "duree_sec": 180 },
  "joueurs": [
    { "id_user": 1, "pseudo": "pierre", "mots": ["OUI", "VIE", "VIRE"], "score": 3 },
    { "id_user": 2, "pseudo": "lou",    "mots": ["QUE", "VIE"],        "score": 2 }
  ],
  "gagnant": 1
}');
INSERT INTO Partie (donnees) VALUES ('{
  "grille": {
    "hauteur": 4,
    "largeur": 4,
    "cases": ["G","A","I","R","R","U","V","E","QU","E","O","T","A","S","M","J"]
  },
  "parametres": { "langue": "fr", "taille_min": 3, "duree_sec": 180 },
  "joueurs": [
    { "id_user": 2, "pseudo": "lou", "mots": ["OUI", "VIE", "VIRE"], "score": 3 },
    { "id_user": 3, "pseudo": "jean",    "mots": ["QUE", "VIE"],        "score": 2 }
  ],
  "gagnant": 2
}');

INSERT INTO jouer (id_user, id_partie) VALUES (1, 1), (2, 1), (2, 2), (3, 2);