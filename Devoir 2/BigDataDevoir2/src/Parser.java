import java.util.ArrayList;

public class Parser {
    public static ArrayList<String> parseBestiaryLinks(String codeSource){
        ArrayList<String> result = new ArrayList<String>();
        String sentence = "<div id=" + Character.toString((char)34)
                + "monster-index-wrapper" + Character.toString((char)34) + " class="
                +  Character.toString((char)34) + "index"+  Character.toString((char)34) + ">";
        String[] groupes = codeSource.split(sentence);
        groupes = groupes[1].split("/div>");
        groupes = groupes[0].split("\n");
        for (String line: groupes) {
            String[] data = line.split("<a href=" + Character.toString((char)34));
            if(data.length != 1){
                String[]link = data[1].split(Character.toString((char)34) + ">");
                if(link[0].indexOf("#") != -1)
                    result.add(link[0]);
            }
        }
        return result;
    }

    public static CreatureJava parseBestiary(String codeSource, String link) throws Exception{
        CreatureJava crea = new CreatureJava();
        String[] d = link.split("#");
        String name = d[1];
        crea.setName(name);

        String[] data = codeSource.split("<h1 id=" +Character.toString((char)34));
        //Cela veut dire que l'on est sur une page a plusieurs monstres
        if(data.length != 2){
            int number = -1;
            for(int i = 1; i<data.length; i++){
                String[] s = data[i].split(Character.toString((char)34) + ">" );
                if(s[0].indexOf(name) != -1){
                    number = i;
                    break;//Sert à rien de continuer
                }
            }
            if(number == -1)
                throw new Exception("Probleme de nom");

            //Pour vérifier si le monstre possède des sorts
            if((data[number].indexOf("spell") == -1 && data[number].indexOf("Spell") == -1)
                    || data[number].indexOf("Offense") == -1)
                return crea;

            String l = "<p class = " + Character.toString((char)34)
                    + "stat-block-breaker" + Character.toString((char)34)
                    + ">Offense</p>";
            data = data[number].split(l);
            l = "<p class = " + Character.toString((char)34)
                    + "stat-block-breaker" + Character.toString((char)34)
                    + ">";
            data = data[1].split(l);

            String[] fSpells = data[0].split("Spell-Like Abilities");
            if(fSpells.length != 1){
                String[] end =  fSpells[1].split("<p class=" + Character.toString((char)34) + "stat-block-1");
                if(end.length == 1){
                    String[] g =fSpells[1].split("p class=" + Character.toString((char)34)
                            + "stat-block-2"+Character.toString((char)34) + ">");
                    for(int i = 1; i< g.length; i++){
                        g = g[i].split("i><a");
                        for(int j = 1; j<g.length; j++){
                            String[] h = g[j].split(">");
                            h = h[1].split("</a");
                            crea.addSpell(h[0]);
                        }
                    }
                }
                else{
                    String[] g =fSpells[1].split("p class=" + Character.toString((char)34)
                            + "stat-block-2"+Character.toString((char)34) + ">");
                    for(int i = 1; i< g.length; i++){
                        g = g[i].split("i><a");
                        for(int j = 1; j<g.length; j++){
                            String[] h = g[j].split(">");
                            h = h[1].split("</a");
                            crea.addSpell(h[0]);
                        }
                    }
                }

            }

            String[] sSpells = data[0].split("Spells Prepared");
            if(sSpells.length != 1){
                String[] g =sSpells[1].split("p class=" + Character.toString((char)34)
                        + "stat-block-2"+Character.toString((char)34) + ">");
                for(int i = 1; i< g.length; i++){
                    g = g[i].split("i><a");
                    for(int j = 1; j<g.length; j++){
                        String[] h = g[j].split(">");
                        h = h[1].split("</a");
                        crea.addSpell(h[0]);
                    }
                }
            }
        }
        //Cela veut dire qu'il n'y a qu'un seul monstre présenté sur la page
        else{
            if((data[1].indexOf("spell") == -1 && data[1].indexOf("Spell") == -1)
                    || data[1].indexOf("Offense") == -1)
                return crea;

            String l = "<p class = " + Character.toString((char)34)
                    + "stat-block-breaker" + Character.toString((char)34)
                    + ">Offense</p>";
            data = data[1].split(l);
            l = "<p class = " + Character.toString((char)34)
                    + "stat-block-breaker" + Character.toString((char)34)
                    + ">";
            data = data[1].split(l);

            String[] fSpells = data[0].split("Spell-Like Abilities");
            if(fSpells.length != 1){
                String[] end =  fSpells[1].split("<p class=" + Character.toString((char)34) + "stat-block-1");
                if(end.length == 1){
                    String[] g =fSpells[1].split("p class=" + Character.toString((char)34)
                            + "stat-block-2"+Character.toString((char)34) + ">");
                    for(int i = 1; i< g.length; i++){
                        g = g[i].split("i><a");
                        for(int j = 1; j<g.length; j++){
                            String[] h = g[j].split(">");
                            h = h[1].split("</a");

                            //System.out.println(h[0]);
                            crea.addSpell(h[0]);
                        }
                    }
                }
                else{
                    String[] g =fSpells[1].split("p class=" + Character.toString((char)34)
                            + "stat-block-2"+Character.toString((char)34) + ">");
                    for(int i = 1; i< g.length; i++){
                        g = g[i].split("i><a");
                        for(int j = 1; j<g.length; j++){
                            String[] h = g[j].split(">");
                            h = h[1].split("</a");
                            //System.out.println(h[0]);
                            crea.addSpell(h[0]);
                        }
                    }
                }

            }

            String[] sSpells = data[0].split("Spells Prepared");
            if(sSpells.length != 1){
                String[] g =sSpells[1].split("p class=" + Character.toString((char)34)
                        + "stat-block-2"+Character.toString((char)34) + ">");
                for(int i = 1; i< g.length; i++){
                    g = g[i].split("i><a");
                    for(int j = 1; j<g.length; j++){
                        String[] h = g[j].split(">");
                        h = h[1].split("</a");
                        //System.out.println(h[0]);
                        crea.addSpell(h[0]);
                    }
                }
            }

        }
        return crea;
    }
}
