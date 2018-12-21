package Exercice1;

public class SpellJava {
    private String name = "";
    private boolean spellResistance = false;

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public boolean getSpellResitance(){
        return spellResistance;
    }
    public void setSpellResistance(boolean spellResitance){
        this.spellResistance = spellResitance;
    }
    @Override
    public String toString(){
        return name  + " " + spellResistance;
    }
}
