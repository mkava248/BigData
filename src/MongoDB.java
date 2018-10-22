import java.util.ArrayList;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.client.MapReduceIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDB {

	private MongoClient client;
	private MongoDatabase db;
	@SuppressWarnings("rawtypes")
	private MongoCollection collection;

	/**
	 * Connexion a MongoDB pour la partie 1
	 */
	public void connectPartie1() {

		client = new MongoClient("localhost", 27017);
		db = client.getDatabase("Devoir1");

		collection = db.getCollection("SpellsCollection");
		collection.drop();

	}
	
	/**
	 * Connexion a MongoDB pour la partie 2
	 */
	public void connectPartie2() {

		client = new MongoClient("localhost", 27017);
		db = client.getDatabase("Devoir1");

		collection = db.getCollection("PageRankCollection");
		collection.drop();

	}

	/**
	 * Ferme la connexion
	 */
	public void close() {
		client.close();
	}

	/**
	 * Inserer un sort dans le MongoDB
	 * 
	 * @param s (Spell)
	 */
	@SuppressWarnings("unchecked")
	public void insertSpell(Spell s) {

		Document d = new Document();
		d.put("name", s.getName());
		d.put("level", s.getLevel());
		d.put("spellresit", s.isSpell_resistance());

		Document components = new Document();

		int size = s.numberComponents();
		for (int i = 0; i < size; i++) {
			try {
				components.put(Integer.toString(i), s.getComponent(i));
			} catch (Exception e) {

			}
		}
		d.put("components", components);

		collection.insertOne(d);
	}

	/**
	 * Permet de faire le map/reduce pour la partie 1
	 */
	public void getSpells() {
		
		String map = 
				"function () { " +
						"if(this.level < 5 && this.components[0] == 'V'){" +
							"if(this.components.hasOwnProperty('1'))" + //cela veut dire qu'il n'y a pas que verbal
								"emit('Pas un bon sort', 1);" +
							"else " +
								"emit(this.name, 1);" +
							"}" +
						"else " +
							"emit('Pas un bon sort', 1);" +
				"}";

		String reduce = 
				"function(key, values) {" +
						"return Array.sum(values);" +
				" }";
				
		@SuppressWarnings("rawtypes")
		MapReduceIterable resultat = collection.mapReduce(map, reduce);

		for (Object object : resultat) {
			Document d = (Document)object;
			System.out.println(d.getString("_id"));
		}
	}
	
	/**
	 * Permet d'inserer une page dans le MongoDb
	 */
	@SuppressWarnings("unchecked")
	public void insertPage(Page page) {
		Document d = new Document();
		d.put("name", page.getName());
		d.put("nbLink", page.getNbLink());
		d.put("pageRank", page.getPageRank());
		collection.insertOne(d);
	}

	/**
	 * Permet d'obtenir les pages ranks des pages
	 * 
	 * @return ArrayList<Double>
	 */
	public ArrayList<Double> getPagesRanks() {
		
		ArrayList<Double> retour = new ArrayList<Double>();

		String map = "function () {" + 
						"emit(this.name, 0.15+0.85*this.pageRank/this.nbLink);" + 
				"}";

		String reduce = "function(key, values) {" + 
				"return Array.sum(values);" +
				" }";

		@SuppressWarnings("rawtypes")
		MapReduceIterable resultat = collection.mapReduce(map, reduce);
		
		for (Object object : resultat) {
			Document d = (Document)object;
			retour.add(d.getDouble("value"));
		}
		
		return retour;

	}
	
	/**
	 * Permet de faire l'update d'une page
	 * 
	 * @param name (String)
	 * @param pagerank (double)
	 */
	public void updatePagesRanks(String name, double pagerank){
		Bson filter = new Document("name",name);
		Bson newPageRank = new Document("pageRank", pagerank);
		
		Bson update = new Document("$set", newPageRank);
		
		collection.updateOne(filter, update);
	}

}