
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
	 * @param s
	 *            (Spell)
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

		String map = "function () { "
				+ "if(this.level < 5 && this.components[0] == 'V'){"
				+ "if(this.components.hasOwnProperty('1'))"
				+ "emit('Pas un bon sort', 1);" + "else " + "emit(this.name, 1);"
				+ "}"
				+ "else " + "emit('Pas un bon sort', 1);" + "}";

		String reduce = "function(key, values) {" + "return Array.sum(values);"
				+ " }";

		@SuppressWarnings("rawtypes")
		MapReduceIterable resultat = collection.mapReduce(map, reduce);

		for (Object object : resultat) {
			Document d = (Document) object;
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
		d.put("nbLink", page.getNbLinkIn());
		d.put("pageRank", page.getPageRank());

		Document links = new Document();

		for (int i = 0; i < page.getNbLinkIn(); i++) {
			Document object = new Document();
			object.put("PageRank", page.getLink(i)[0]);
			object.put("NbLinks", page.getLink(i)[1]);
			links.put(Integer.toString(i), object);
		}
		d.put("links", links);

		collection.insertOne(d);
	}

	/**
	 * Permet d'obtenir les pages ranks des pages
	 * 
	 * @return ArrayList<Double>
	 */
	public double getPageRank(String name) {

		String map = "function ("+name+") {" 
				+ "var s = 0;" +
				"if(this.name == '"+ name +"'){" 
					+ "for(var i = 0; i<this.nbLink; i++){"
						+ "s=s+this.links[i].PageRank/this.links[i].NbLinks;" 
					+ "}"
					+ "s=s*0.85+0.15;" 
					+ "emit(this.name, s);" 
				+ "}"
			+ "}";

		String reduce = "function(key, values)" + " {"
				+ "return values;" + "}";

		@SuppressWarnings("rawtypes")
		MapReduceIterable resultat = collection.mapReduce(map, reduce);

		for (Object object : resultat) {
			Document d = (Document) object;
			return d.getDouble("value");
		}

		return 0;

	}

	/**
	 * Permet de faire l'update d'une page
	 * 
	 * @param name
	 *            (String)
	 * @param pagerank
	 *            (double)
	 */
	public void updatePagesRanks(String name, double pagerank) {
		//update du PR de la page
		Bson filter = new Document("name", name);
		Bson newPageRank = new Document("pageRank", pagerank);

		Bson update = new Document("$set", newPageRank);
		collection.updateOne(filter, update);
		
		//Mise à jour de la PR de la page courante  enregistrée dans les autres pages 

		
		//update du PR enregistré dans les autres Page
		if(name.equals("PageA")){
			filter = new Document("name", "PageB");
			//contient la nouvelle PR de A pour l'enregistrer dans B.
			update = new Document("$set", new Document("links.0.PageRank", pagerank));
			collection.updateOne(filter, update);
			
			filter = new Document("name", "PageC");
			//contient la nouvelle PR de A pour l'enregistrer dans C.
			update = new Document("$set", new Document("links.0.PageRank", pagerank));
			collection.updateOne(filter, update);
		}
		else if(name.equals("PageB")){
			filter = new Document("name", "PageC");
			//contient la nouvelle PR de B pour l'enregistrer dans C.
			update = new Document("$set", new Document("links.1.PageRank", pagerank));
			collection.updateOne(filter, update);
		}
		else if(name.equals("PageC")){
			filter = new Document("name", "PageA");
			//contient la nouvelle PR de C pour l'enregistrer dans A.
			update = new Document("$set", new Document("links.0.PageRank", pagerank));
			collection.updateOne(filter, update);
		}
		else if(name.equals("PageD")){
			filter = new Document("name", "PageC");
			//contient la nouvelle PR de D pour l'enregistrer dans C.
			update = new Document("$set", new Document("links.2.PageRank", pagerank));
			collection.updateOne(filter, update);
		}
		
	}

}