import org.bson.Document;

import com.mongodb.DBCollection;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;
import com.mongodb.MongoClient;
import com.mongodb.client.MapReduceIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDB {

	private MongoClient client;
	private MongoDatabase db;
	@SuppressWarnings("rawtypes")
	private MongoCollection collection;

	public void connect() {

		client = new MongoClient("localhost", 27017);
		db = client.getDatabase("Devoir1");

		collection = db.getCollection("SpellsCollection");
		// collection.drop();

	}

	public void close() {
		client.close();
	}

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
							"emit('Pas bon', 1);" +
				"}";

		String reduce = 
				"function(key, values) {" +
						"return Array.sum(values);" +
				" }";
				
		MapReduceIterable resultat = collection.mapReduce(map, reduce);

		for (Object object : resultat) {
			System.out.println(object);
		}
	}

}