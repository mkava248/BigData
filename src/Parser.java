
public class Parser {
	private String text;
	private Spell spell;
	
	/**
	 * Constructeur du parseur
	 * 
	 * @param text
	 */
	public Parser(String text){
		this.text = text;
		this.spell = null;
	}
	
	/**
	 * Permet d'obtenir le sort si celui est bien un sort de mage
	 * 
	 * @return spell (Spell) || null
	 */
	public Spell parse(){
		
		String[] tab1 = this.text.split("<div class='heading'>");
		if(tab1.length < 2)
			return null;
		//System.out.println(tab1[1]);
		
		String[] tab2 = tab1[1].split("<!-- END Spell -->");
		if(tab2.length < 2)
			return null;
		//System.out.println(tab2[0]);
		
		String[] splitSpell = tab2[0].split("sorcerer/wizard");
		
		//Cela veut dire que le sorcier peut lancer se sort
		if(splitSpell.length > 1){
			
			spell = new Spell();
			
			//Recuperation du nom du sort
			String[] nameSplit = splitSpell[0].split("</p></div>");
			String name = nameSplit[0].substring(3,nameSplit[0].length());	
			
			name = name.replace("'", " "); //Pour éviter des problèmes avec l'insertion dans la db
			spell.setName(name);
			//System.out.println(spell.getName());
			
			//Recuperation du level
			String[] levelSplit = splitSpell[1].split(",");
			if(levelSplit[0].length() > 10){
				String[] levelSplit2 = levelSplit[0].split("</p>");
				spell.setLevel(Integer.parseInt(levelSplit2[0].substring(1,levelSplit2[0].length())));
			}
			else{
				spell.setLevel(Integer.parseInt(levelSplit[0].substring(1,levelSplit[0].length())));
			}
			
			//System.out.println(spell.getLevel());
			
			//Recuperation de la resistance
			String[] resistanceSplit = splitSpell[1].split("<b>Spell Resistance</b>");
			if(resistanceSplit.length > 1){
				String resistanceStr = resistanceSplit[1].substring(1, 2);				
				spell.setSpell_resistance(resistanceStr.contains("y"));
			}
			
			//Recuperation des composants
			String[] componentsSplit = splitSpell[1].split("<b>Components</b>");
			String[] componentsSplit2 = componentsSplit[1].split("</p>");
			String[] componentsSplit3 = componentsSplit2[0].split(",");
			for (int size = 0; size < componentsSplit3.length; size++){
				if(componentsSplit3[size].substring(0, 1).equals(" "))
					if(componentsSplit3[size].substring(1, 2).equals(componentsSplit3[size].substring(1, 2).toUpperCase()))
						if(componentsSplit3[size].substring(1, 2).equals("D") || 
								componentsSplit3[size].substring(1, 2).equals("A"))
							spell.addComponent(componentsSplit3[size].substring(1, 3));
						else
							spell.addComponent(componentsSplit3[size].substring(1, 2));
			}
				
		}
		
		return spell;
	}
}
