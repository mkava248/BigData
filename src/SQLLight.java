import java.sql.*;

import java.util.ArrayList;

public class SQLLight {
	private Connection c = null;
	private Statement stmt = null;
	private boolean connectBool = false;
	private int nbr;

	public void connect() {

		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:test.db");
			stmt = c.createStatement();
			connectBool = true;
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Opened database successfully");

	}

	public void close() {
		try {
			stmt.close();
			c.close();
			connectBool = false;
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void init() {
		if (connectBool) {
			try {
				String sql = "CREATE TABLE IF NOT EXISTS Spell" // IF NOT EXISTS
						+ "(ID INTEGER PRIMARY KEY     AUTOINCREMENT,"
						+ " NAME           TEXT    NOT NULL, "
						+ " LEVEL          INT     NOT NULL, "
						+ " RESISTANCE     INT     NOT NULL)";
				stmt.executeUpdate(sql);

				sql = "CREATE TABLE IF NOT EXISTS Component "
						+ "(ID INTEGER PRIMARY KEY     AUTOINCREMENT,"
						+ " NAME           TEXT    NOT NULL)";
				stmt.executeUpdate(sql);

				sql = "CREATE TABLE IF NOT EXISTS ComponentsBySpells "
						+ "(ID INTEGER PRIMARY KEY     AUTOINCREMENT,"
						+ " INDEXSPELL     INT     NOT NULL,"
						+ " INDEXCOMPONENT INT     NOT NULL)";
				stmt.executeUpdate(sql);

				// Pour connaitre le nombre de sort deja presents
				sql = "SELECT COUNT(*) FROM Spell";
				ResultSet result = stmt.executeQuery(sql);
				nbr = result.getInt(1);

				// Inserer les composants
				sql = "DELETE FROM Component;";
				stmt.executeUpdate(sql);
				sql = "INSERT INTO Component (ID, NAME) VALUES (1,'V');";
				stmt.executeUpdate(sql);
				sql = "INSERT INTO Component (ID, NAME) VALUES (2,'S');";
				stmt.executeUpdate(sql);
				sql = "INSERT INTO Component (ID, NAME) VALUES (3,'M');";
				stmt.executeUpdate(sql);
				sql = "INSERT INTO Component (ID, NAME) VALUES (4,'F');";
				stmt.executeUpdate(sql);
				sql = "INSERT INTO Component (ID, NAME) VALUES (5,'DF');";
				stmt.executeUpdate(sql);
				sql = "INSERT INTO Component (ID, NAME) VALUES (6,'AF');";
				stmt.executeUpdate(sql);

			} catch (Exception e) {
				System.err.println(e.getClass().getName() + ": "
						+ e.getMessage());
				System.exit(0);
			}
			// System.out.println("Tables created successfully");
		}
	}

	public void addSpell(Spell spell) throws Exception {
		try {
			int resistanceInt = spell.isSpell_resistance() ? 1 : 0;
			String sql = "SELECT * FROM Spell WHERE NAME = '" + spell.getName()
					+ "';";
			ResultSet result = stmt.executeQuery(sql);
			Object obj = result.getObject(1);
			if (obj == null) {
				sql = "INSERT INTO Spell  (NAME,LEVEl, RESISTANCE) VALUES ('"
						+ spell.getName() + "', " + spell.getLevel() + ", "
						+ resistanceInt + ");";
				stmt.executeUpdate(sql);
				nbr += 1;

				for (int i = 0; i < spell.numberComponents(); i++) {
					String component = spell.getComponent(i);
					sql = "SELECT ID FROM Component WHERE NAME = '" + component
							+ "';";
					result = stmt.executeQuery(sql);
					int id = result.getInt(1);
					sql = "INSERT INTO ComponentsBySpells ( INDEXSPELL, INDEXCOMPONENT) VALUES ( "
							+ nbr + ", " + id + ");";

					stmt.executeUpdate(sql);
				}		
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	public ArrayList<String> getSpellByContraints() throws Exception{
		ArrayList<String> spells = new ArrayList<String>();
		try{
			String sql = "SELECT Spell.NAME FROM Spell INNER JOIN ComponentsBySpells " +
					"ON Spell.ID = ComponentsBySpells.INDEXSPELL " +
					"WHERE Spell.LEVEL < 5 GROUP BY Spell.ID " +
					"HAVING COUNT(Spell.ID) = 1 AND ComponentsBySpells.INDEXCOMPONENT = 1;";
			
			ResultSet result = stmt.executeQuery(sql);
			while(result.next()){
				spells.add(result.getString(1));
			}
		}catch(SQLException e){
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}		
		return spells;
	}
}