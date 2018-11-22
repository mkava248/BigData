import java.util.ArrayList;


public class Spell {
	private String name;
	private int level;
	private boolean spell_resistance;
	private ArrayList<String> components;
	
	/**
	 * Constructeur du sort
	 */
	public Spell(){
		this.name = "unknow";
		this.level = 0;
		this.spell_resistance = false;
		components = new ArrayList<String>();
	}
	
	/**
	 * Constructeur du sort avec ses paramètres
	 * 
	 * @param name (String)
	 * @param level (int)
	 * @param spell_resistance (boolean)
	 */
	public Spell(String name, int level, boolean spell_resistance){
		this.name = name;
		this.level = level;
		this.spell_resistance = spell_resistance;
	}

	/**
	 * Obtenir le nom
	 * 
	 * @return name (String)
	 */
	public String getName() {
		return name;
	}

	/**
	 * Fixe le nom
	 * 
	 * @param name (String)
	 */
	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public boolean isSpell_resistance() {
		return spell_resistance;
	}

	public void setSpell_resistance(boolean spell_resistance) {
		this.spell_resistance = spell_resistance;
	}
	
	public void addComponent(String component){
		this.components.add(component);
	}
	
	public String getComponent(int index) throws Exception{
		if (index < 0  ||index > this.components.size()-1){
			throw new Exception("Bad index");
		}			
		return (String)this.components.get(index);
	}
	
	public int numberComponents(){
		return this.components.size();
	}
	
	public String toString(){
		String value = "Name : " + this.name + " ;Level : " + Integer.toString(this.level) + 
				" ;Resistance : " +Boolean.toString(this.spell_resistance) + 
				" ;Component(s) : ";
		for(int i = 0; i < this.components.size(); i++){
			try {
				value += getComponent(i) + ", ";
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return value;
	}
}
