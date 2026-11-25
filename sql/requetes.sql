SELECT id_partie, donnees->'parametres'->>'langue' AS langue FROM Partie;

SELECT id_partie, jsonb_array_length(donnees->'joueurs') AS nb_joueurs FROM Partie;
/* on compte le nb delements de la liste joueurs pr avoir le nb de joueurs*/

SELECT id_partie FROM Partie WHERE donnees @> '{"gagnant": 1}';
SELECT id_partie FROM Partie WHERE donnees @> '{"gagnant": 2}';
SELECT id_partie FROM Partie WHERE donnees @> '{"gagnant": 3}';

SELECT id_partie FROM Partie WHERE donnees->'joueurs' @> '[{"id_user": 3}]';

SELECT joueur->>'pseudo' AS pseudo, joueur->>'score' AS score
FROM Partie, jsonb_array_elements(donnees->'joueurs') AS joueur
WHERE id_partie = 1; 
/* une ligne par joueur, le pseudo et le score de chaque joueur de la partie id 1 */