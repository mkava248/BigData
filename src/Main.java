import java.util.ArrayList;

public class Main {
	public static void main(String[] args) {

		SQLLight db = new SQLLight();
		db.connect();
		db.init();

		String url, code_source;
		GrabHTML grab = new GrabHTML();
		ArrayList<String> spells;
		try {
			// Calling the Connect method
			for (int i = 1; i < 1976; i++) { // max : 1976
				// System.out.println(i);
				url = "http://www.dxcontent.com/SDB_SpellBlock.asp?SDBID=" + i;
				code_source = grab.Connect(url);
				Parser parser = new Parser(code_source);
				Spell spell = parser.Parse();
				if (spell != null) {
					// System.out.println(i);
					db.addSpell(spell);
				}

			}
			spells = db.getSpellByContraints();
			db.close();
			for (String name : spells) {
				System.out.println(name);
			}
			System.out.println("Finish");

		} catch (Exception e) {

		}
	}
}
