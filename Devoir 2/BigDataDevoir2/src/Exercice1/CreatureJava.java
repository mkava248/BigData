package Exercice1;

import java.util.ArrayList;

//Cette classe est utilisé pour faire le lien entre le java qui va récupérer sur le site, et la classe créature faite en scal
public class CreatureJava {
    private String name;
    private ArrayList<String> spells = new ArrayList<String>();

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public ArrayList<String> getSpells(){
        return spells;
    }
    public void addSpell(String spell){ spells.add(spell);}
    public int getSize(){return spells.size();}
    public String getSpell(int i){return spells.get(i);}

    @Override
    public String toString(){
        String r = "[Name : " + name + "]; [Spells : ";
        for (String spell : spells) {
            r += spell + ", ";
        }
        r += "]";
        return r;
    }
}
