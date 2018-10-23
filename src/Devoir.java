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
					//System.out.println(i);
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
		
		Page a = new Page("PageA", 2, 10);
		Page b = new Page("PageB", 1, 10);
		Page c = new Page("PageC", 1, 10);
		Page d = new Page("PageD", 1, 10);
		
		//Ajout des liens
		
		a.addLink(c.getPageRank(), c.getNbLinkOut());
		b.addLink(a.getPageRank(), a.getNbLinkOut());
		c.addLink(a.getPageRank(), a.getNbLinkOut());
		c.addLink(b.getPageRank(), b.getNbLinkOut());
		c.addLink(d.getPageRank(), d.getNbLinkOut());
		
		//Insertion 
		
		mongo.insertPage(a);
		mongo.insertPage(b);
		mongo.insertPage(c);
		mongo.insertPage(d);
		
		
		
		for(int i = 0; i<20; i++){
			
			for (int j = 0; j < 4; j++){
				String name = "Page" + Character.toString((char) (65+j));
				double pageRank = mongo.getPageRank(name);
				mongo.updatePagesRanks(name, pageRank);
			}
		}
		
		
		System.out.println("Page A : " + mongo.getPageRank("PageA"));
		System.out.println("Page B : " + mongo.getPageRank("PageB"));
		System.out.println("Page C : " + mongo.getPageRank("PageC"));
		System.out.println("Page D : " + mongo.getPageRank("PageD"));
	}
		
}
