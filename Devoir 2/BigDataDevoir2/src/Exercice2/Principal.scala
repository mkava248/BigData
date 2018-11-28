package Exercice2
import scala.collection.mutable.ArrayBuffer
import org.apache.spark.graphx.{Edge, EdgeContext, Graph, VertexId, TripletFields}
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable

//tiebreak value is generated randomly before every graph iteration
class node(val id: Int, val color: Int = 1, val knighthood: Boolean = false, val tiebreakValue: Long = 1L) extends Serializable {
  override def toString: String = s"id : $id tiebreakValue : $tiebreakValue color : $color knighthood : $knighthood"
}

/*def sendTieBreakValues(ctx: EdgeContext[node, String, Long]): Unit = {
if (ctx.srcAttr.knighthood == false && ctx.dstAttr.knighthood == false) {
ctx.sendToDst(ctx.srcAttr.tiebreakValue)
ctx.sendToSrc(ctx.dstAttr.tiebreakValue)
}
}*/



object Principal {

  def generateFirstFight(): ArrayBuffer[Personnage] ={

      var tab = new ArrayBuffer[Personnage]()
      //Generation de Solar
      val greatsWord = new Weapon("greatsWord", Array(35, 30, 25, 20), 10, 5)
      val slam = new Weapon("slam", Array(30), 10, 1)
      val longBow = new Weapon("longBow", Array(31, 26, 21, 16), 10, 5)
      val weaponMap = Array(greatsWord, slam, longBow)
      val solar = new Solar("Solar", 363, 44,15, weaponMap, 0, 0, 0)

      //Generation des worgs rider
      val arrayOrc = ArrayBuffer[Orc]()
      val battleAxe = new  Weapon("battleAxe", Array(2), 2, 2)
      (1 to 9) foreach(x => {arrayOrc += new WorgRider("WordRider_"+x, 13, 18, Array(battleAxe), 5, 5, 10)})

      //Generation des barbares orcs
      val arrayBarbarian = ArrayBuffer[Orc]()
      val doubleAxe = new  Weapon("battleAxe", Array(2), 2, 2)
      val other = new  Weapon("battleAxe", Array(2), 2, 2)
      (1 to 4) foreach(x => {arrayOrc += new WorgRider("Barbarian_"+x, 142, 17, Array(doubleAxe, other), 10, 10, 10)})

      //Generation du warlord
      val viciousFlail = new Weapon("viciousFlail", Array(1), 2, 2)
      val lionsShield = new Weapon("lionsShield", Array(1), 2, 2)
      val warlord = new Warlord("Warlord", 141, 27, Array(viciousFlail, lionsShield), 20, 20, 10)

      //Affichage
      /*println(solar.toString()+"\n")
      println(arrayOrc.mkString("\n\n"))
      println(arrayBarbarian.mkString("\n\n"))
      println(warlord.toString)*/

      tab += solar
      tab += warlord
      tab.appendAll(arrayBarbarian)
      tab.appendAll(arrayOrc)
      tab
  }


  //TODO utiliser un map ou un flatmpap à la place du foreach
  def generateEdge(vertices: ArrayBuffer[(Long, Personnage)]): ArrayBuffer[Edge[Int]] ={
    val a = new ArrayBuffer[Edge[Int]]()
    1 to vertices.length-1 foreach (i => a.append(Edge(vertices(0)._1.toLong, vertices(i)._1.toLong, vertices(0)._2.calculateDistance(vertices(i)._2))))
    a
  }

  def sendPosition(context: EdgeContext[Personnage, Int, Long]): Unit={
    context.sendToSrc(context.attr)
    context.sendToDst(context.attr)
//    println("Source : " + context.srcAttr.toString)
//    println("Destination : " + context.dstAttr.toString)
//    println("Distance : " +context.attr.toString)
//    print("\n\n")
  }

  def sendAttack(context: EdgeContext[Personnage, Int, Long]): Unit ={
    context.sendToDst(context.attr)
  }


  def selectTheClosest(n:Long, m:Long): Long ={
    //retourne le minimum dans cet exemple
    if (n < m) n
    else m
  }


  def execute(myGraph: Graph[Personnage, Int], maxIterations: Int, context: SparkContext): Unit ={
    var counter = 0
    val fields = new TripletFields(true, true, false) //join strategy

    def loop1: Unit = {
      while (true) {

        println("ITERATION NUMERO : " + (counter + 1))
        counter += 1
        if (counter >= maxIterations) return

        var messages = myGraph.aggregateMessages[Long](
          sendPosition,
          selectTheClosest
          //fields //use an optimized join strategy (we don't need the edge attribute)
        )

        if (messages.isEmpty()) return
/*
        myGraph2 =
        messages = myGraph.aggregateMessages[Long](
          sendAttack,
          selectTheClosest
          //fields //use an optimized join strategy (we don't need the edge attribute)
        )

        if (messages.isEmpty()) return

        myGraph = myGraph.((a, b, c) => (a.))
*/



        //Ignorez : Code de debug
        /*var printedGraph = myGraph.vertices.collect()
        printedGraph = printedGraph.sortBy(_._1)
        printedGraph.foreach(
          elem => println(elem._2)
        )*/
      }

    }

    loop1 //execute loop
    myGraph //return the result graph
  }


  def main(args:Array[String]): Unit = {

    val conf = new SparkConf().setAppName("Petersen Graph (10 nodes)").setMaster("local[*]")
    val sc = new SparkContext(conf)
    sc.setLogLevel("ERROR")


    //Definition des sommets
    var myVertices = generateFirstFight().zipWithIndex.map{case (creature, index) => (index.toLong, creature)}
    myVertices.foreach(x=>println("\nID = " + x._1 + ", " + x._2.toString))

    //Definition des arretes
    var myEdges = generateEdge(myVertices)
    myEdges.foreach(x=>println(x.toString))
    var myGraph = Graph(sc.makeRDD(myVertices), sc.makeRDD(myEdges))
    val res = execute(myGraph, 20, sc)

  }
}
