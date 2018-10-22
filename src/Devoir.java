import java.util.ArrayList;


public class Devoir {
	
	/**
	 * Pour lancer la partie 1 du devoir
	 */
	public static void Partie1(){
		SQLLight dbSQL = new SQLLight();
		dbSQL.connect();
		dbSQL.init();

		MongoDB mongo = new MongoDB();
		mongo.connectPartie1();
		
		String url, code_source;
		ArrayList<String> spells;
		try {
			// Calling the Connect method
			for (int i = 1; i < 1976; i++) { // max : 1976
				// System.out.println(i);
				url = "http://www.dxcontent.com/SDB_SpellBlock.asp?SDBID=" + i;
				code_source = GetSource.getCode(url);
				Parser parser = new Parser(code_source);
				Spell spell = parser.parse();
				if (spell != null) {
					System.out.println(i);
					dbSQL.addSpell(spell);
					mongo.insertSpell(spell);
				}

			}
			spells = dbSQL.getSpellByContraints();
			dbSQL.close();
			System.out.println("///////Réponse via MongoDB///////");
			mongo.getSpells();
			System.out.println("///////Réponse via SQLite///////");
			for (String name : spells) {
				System.out.println(name);
			}
			System.out.println("Finish");

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		mongo.close();
	}
	
	/**
	 * Pour lancer la partie 2 du devoir
	 */
	public static void Partie2(){
		MongoDB mongo = new MongoDB();
		mongo.connectPartie2();
		
		ArrayList<Double> pagesRanks;
		
		mongo.insertPage(new Page("PageA", 2, 10));
		mongo.insertPage(new Page("PageB", 1, 10));
		mongo.insertPage(new Page("PageC", 1, 10));
		mongo.insertPage(new Page("PageD", 1, 10));
		
		
		for(int i = 0; i<20; i++){
			pagesRanks = mongo.getPagesRanks();
			
			for (int j = 0; j < 4; j++){
				double pageRank = pagesRanks.get(j);
				String name = "Page" + Character.toString((char) (65+j));
				mongo.updatePagesRanks(name, pageRank);
			}
		}
		
		pagesRanks = mongo.getPagesRanks();
		System.out.println(pagesRanks);
	}

			

			/*private static void changerPageRank(int pageRank, String PageName) {
				// insérer pagerank dans la base de donnée correspondant à la page PageName
			}


				for (int i = 0; i < 20; i++) {
					changerPageRank(getPageRank("PageA"), "PageA");
					changerPageRank(getPageRank("PageB"), "PageB");
					changerPageRank(getPageRank("PageC"), "PageC");
					changerPageRank(getPageRank("PageD"), "PageD");
				}*/
		
}
