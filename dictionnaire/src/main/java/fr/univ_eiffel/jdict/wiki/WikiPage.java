package fr.univ_eiffel.jdict.wiki;

/** Une page du dump : son titre, son espace de noms (0 = article) et son texte MediaWiki. */
public record WikiPage(String title, int namespace, String text) {
}
