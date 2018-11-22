import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ArrayBuffer

object test extends App {

 val string = "http://legacy.aonprd.com/bestiary/"
 val code = GetSource.getCode(string + "monsterIndex.html")
 val r = Parser.parseBestiaryLinks(code)

 val creatures = ArrayBuffer[Creature]()

 var i = 0
 for (i <- 0 until r.size()) {
  val codeBeast = GetSource.getCode(string + r.get(i))
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


 val conf = new SparkConf().setAppName("petit test rapide").setMaster("local[*]")
 val sc = new SparkContext(conf)
 sc.setLogLevel("ERROR")


 /*
  println("Hello world !")
  var c1 = new Creature("Solar")
  c1.addSpell("etherealness")
  c1.addSpell("wind wall")
  c1.addSpell("miracle")
  c1.addSpell("storm  of vengeance")
  c1.addSpell("fire storm")
  c1.addSpell("holy aura")
  c1.addSpell("mass cure critical")

  var c2 = new Creature("Bralani")
  c2.addSpell("blur")
  c2.addSpell("charm person")
  c2.addSpell("mass cure critical")
  c2.addSpell("mirror image")
  c2.addSpell("wind wall")


  val creatures = ArrayBuffer[Creature]()
  creatures +=c1
  creatures +=c2*/
 val rdd = sc.parallelize(creatures)
 //rdd = sc.parallelize(Array(c1, c2))
 /*def g(creature:Creature) = {
   val a = Array()
   for(int i = 0; i<creature._spells.length;i++){
     a.addString(creature._spells, creature._name)
   }
 }*/

 val temp = rdd.flatMap(creature => creature._spells.map(s => (s, creature._name)))
   //spells.foreach(s =>(s, creature._name)))
   .reduceByKey((res, n) => res +" + "+ n)
 //print(temp)
 //temp.saveAsTextFile("IndiceInverses")
 temp.foreach(println(_))
}