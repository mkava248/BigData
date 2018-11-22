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
      //Pour faire le lien avec la classe Creature de Scala
      val crea = new Creature(beast.getName())
      val j = 0
      for (j <- 0 until beast.getSize()){
        crea.addSpell(beast.getSpell(j))
      }
      creatures += crea
    }

  }
}
