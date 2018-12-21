package Exercice1

import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ArrayBuffer

object Exercice1 extends App {

 val bestiary = "http://legacy.aonprd.com/bestiary/"
 val code = GetSource.getCode(bestiary + "monsterIndex.html")
 val r = Parser.parseBestiaryLinks(code)

 val spellsBook = "http://www.dxcontent.com/SDB_SpellBlock.asp?SDBID="

 val creatures = ArrayBuffer[Creature]()
 val spells = ArrayBuffer[Spell]()

 val conf = new SparkConf().setAppName("petit test rapide").setMaster("local[*]")
 val sc = new SparkContext(conf)
 sc.setLogLevel("ERROR")

 //Recuperation des creatures
 println("Récupération des créatures")
 var i = 0
 for (i <- 0 until r.size()) {
  val codeBeast = GetSource.getCode(bestiary + r.get(i))
  val beast = Parser.parseBestiary(codeBeast, r.get(i))
  if(beast != null){
   //println(beast.toString)
   //Pour faire le lien avec la classe Creature de Scala
   val crea = new Creature(beast.getName())
   val j = 0
   for (j <- 0 until beast.getSize()){
    crea.addSpell(beast.getSpell(j))
   }
   creatures += crea
  }

 }

 //Recuperation des sorts
 i = 1
 println("Récupération des sorts")
  for(i <- 1 until 1976){
    val codeSpell = GetSource.getCode(spellsBook + i)
    val spellJ = Parser.parseSpell(codeSpell)
    if(spellJ != null) {
      val spell = new Spell(spellJ.getName().toLowerCase(), spellJ.getSpellResitance)
      spells += spell
    }
  }

 println("Début RDD")

 val rddCrea = sc.parallelize(creatures)
 val rddSpell = sc.parallelize(spells)
 val t = rddSpell.map(s => (s._name, s._resistance))


 val temp = rddCrea.flatMap(creature => creature._spells.map(s => (s, creature._name)))
               .reduceByKey((res, n) => res +" + "+ n).join(t).map(o => (o._1, o._2._2, o._2._1))
 ///temp.foreach(println(_))

 //var test = temp.map(o => (o._1, o._2.toString().equals("true"), o._3))
 //test.foreach(println(_))
/*temp.flatMap(x => {
 if(!x._2) (x._1, x._2, x._3)
} )*/
 var test = temp.filter(x => (!x._2))
 test.foreach(println(_)) //n'affiche pas les sorts de soin car ils ont spell resistance a true


  //-----------------------------------------
  //Fin exercice 1
  //-----------------------------------------

}
