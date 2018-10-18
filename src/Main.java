import java.util.ArrayList;

public class Main {
	public static void main(String[] args) {

		SQLLight dbSQL = new SQLLight();
		dbSQL.connect();
		dbSQL.init();

		MongoDB mongo = new MongoDB();
		mongo.connect();
		
		String url, code_source;
		ArrayList<String> spells;
		try {
			// Calling the Connect method
			/*for (int i = 1; i < 1976; i++) { // max : 1976
				// System.out.println(i);
				url = "http://www.dxcontent.com/SDB_SpellBlock.asp?SDBID=" + i;
				code_source = GetSource.getCode(url);
				Parser parser = new Parser(code_source);
				Spell spell = parser.parse();
				if (spell != null) {
					System.out.println(i);
					dbSQL.addSpell(spell);
					mongo.insertSpell(spell);
					//System.out.println(spell);
				}

			}*/
			spells = dbSQL.getSpellByContraints();
			dbSQL.close();
			mongo.getSpells();
			for (String name : spells) {
				System.out.println(name);
			}
			System.out.println("Finish");

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		mongo.close();
	}
}
