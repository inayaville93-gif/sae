-- Affiche les joueurs d'une partie : pseudo, score, mots trouvés, et s'il a gagné.
-- rappel: SELECT * FROM afficher_partie(1);
CREATE OR REPLACE FUNCTION afficher_partie(numero INT)
RETURNS TABLE (pseudo TEXT, score INT, mots JSONB, gagnant BOOLEAN)
LANGUAGE sql
AS $$
    SELECT joueur->>'pseudo',
           (joueur->>'score')::INT,
           joueur->'mots',
           (joueur->>'id_user')::INT = (donnees->>'gagnant')::INT
    FROM Partie, jsonb_array_elements(donnees->'joueurs') AS joueur
    WHERE id_partie = numero;
$$;