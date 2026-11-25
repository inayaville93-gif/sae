DROP TABLE IF EXISTS jouer, Partie, Utilisateur;
CREATE TABLE Utilisateur(
   id_user SERIAL,
   pseudo VARCHAR(30) NOT NULL,
   mdp_hash VARCHAR(255) NOT NULL,
   date_inscription TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   profil_public BOOLEAN NOT NULL DEFAULT TRUE,
   image VARCHAR(255),
   adresse_mail VARCHAR(100) NOT NULL,
   PRIMARY KEY(id_user),
   UNIQUE(pseudo),
   UNIQUE(adresse_mail)
);

CREATE TABLE Partie(
   id_partie SERIAL,
   date_partie TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   donnees JSONB NOT NULL,
   PRIMARY KEY(id_partie)
);

CREATE TABLE jouer(
   id_user INT,
   id_partie INT,
   PRIMARY KEY(id_user, id_partie),
   FOREIGN KEY(id_user) REFERENCES Utilisateur(id_user),
   FOREIGN KEY(id_partie) REFERENCES Partie(id_partie)
);

CREATE INDEX idx_partie_donnees ON Partie USING GIN (donnees);
CREATE INDEX idx_jouer_partie ON jouer (id_partie);

CREATE INDEX idx_jouer_partie ON jouer (id_partie);

/* type GIN pour ranger cle et valeur dans JSON (plein de valeur possible dans ce type dindex)*/
/* et ca sert a cherhcer + vite avc loperateur @> (contient v ou f)*/