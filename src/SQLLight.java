import java.sql.*;

import java.util.ArrayList;

public class SQLLight {
	private Connection c = null;
	private Statement stmt = null;
	private boolean connectBool = false;
	private int nbr;

	/**
	 * Permet de se connecter à la base de données SQLLite. Créer la base si
	 * celle n'existe pas
	 */
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

	/**
	 * Ferme la base de données pour ne pas avoir de problème
	 */
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

	/**
	 * Créer toutes les tables. Rajoute les données dans la table Components
	 */
	public void init() {
		if (connectBool) {
			try {
				//stmt.execute("DROP TABLE Spell");
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

				//stmt.execute("DROP TABLE ComponentsBySpells");
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

	/**
	 * Ajoute un spell dans table Spell et fait les liaisons avec les composants
	 * dans la table ComponentsBySpells
	 * 
	 * @param spell
	 *            (Spell)
	 * @throws Exception
	 */
	public void addSpell(Spell spell) throws Exception {
		try {
			int resistanceInt = spell.isSpell_resistance() ? 1 : 0;
			String sql = "SELECT * FROM Spell WHERE NAME = '" + spell.getName()
					+ "';";
			ResultSet result = stmt.executeQuery(sql);
			//Object obj = result.getObject(1);
			// if (obj == null) {
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
			// }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * Applique la requête demandée pour le devoir 1. Les sorts sont de sorts de
	 * sorcier, de niveau 4 maximum, et étant seulement verbaux.
	 * 
	 * @return spells (ArrayList<Spell>)
	 * @throws Exception
	 */
	public ArrayList<String> getSpellByContraints() throws Exception {
		ArrayList<String> spells = new ArrayList<String>();
		try {
			// Permet de recuperer tous les sorts mage respectant les
			// contraintes
			String sql = "SELECT Spell.NAME FROM Spell INNER JOIN ComponentsBySpells "
					+ "ON Spell.ID = ComponentsBySpells.INDEXSPELL "
					+ "WHERE Spell.LEVEL < 5 GROUP BY Spell.ID "
					+ "HAVING COUNT(Spell.ID) = 1 AND ComponentsBySpells.INDEXCOMPONENT = 1;";

			ResultSet result = stmt.executeQuery(sql);
			while (result.next()) {
				spells.add(result.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return spells;
	}
}