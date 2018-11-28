package Exercice2
import scala.collection.mutable.ArrayBuffer
import org.apache.spark.graphx.{Edge, EdgeContext, Graph, VertexId}
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
      val solar = new Solar("Solar", 363, 44,15, weaponMap, 0, 0)

      //Generation des worgs rider
      val arrayOrc = ArrayBuffer[Orc]()
      val battleAxe = new  Weapon("battleAxe", Array(2), 2, 2)
      (1 to 9) foreach(x => {arrayOrc += new WorgRider("WordRider_"+x, 13, 18, Array(battleAxe), 1, 1)})

      //Generation des barbares orcs
      val arrayBarbarian = ArrayBuffer[Orc]()
      val doubleAxe = new  Weapon("battleAxe", Array(2), 2, 2)
      val other = new  Weapon("battleAxe", Array(2), 2, 2)
      (1 to 4) foreach(x => {arrayOrc += new WorgRider("Barbarian_"+x, 142, 17, Array(doubleAxe, other), 2, 2)})

      //Generation du warlord
      val viciousFlail = new Weapon("viciousFlail", Array(1), 2, 2)
      val lionsShield = new Weapon("lionsShield", Array(1), 2, 2)
      val warlord = new Warlord("Warlord", 141, 27, Array(viciousFlail, lionsShield), 3, 3)

      //Debug
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

  def generateEdge(vertices: ArrayBuffer[(Long, Personnage)]): ArrayBuffer[Edge[Int]] ={
    val a = new ArrayBuffer[Edge[Int]]()
    1 to vertices.length-1 foreach  (i => a.append(Edge(vertices(0)._1.toLong, vertices(i)._1.toLong, vertices(0)._2.calculateDistance(vertices(1)._2))))
    a
  }

  def sendPosition(): Unit={

  }

  def execute(value: Graph[Personnage, String], i: Int, context: SparkContext): Unit ={

  }

  def main(args:Array[String]): Unit = {

    val conf = new SparkConf().setAppName("Petersen Graph (10 nodes)").setMaster("local[*]")
    val sc = new SparkContext(conf)
    sc.setLogLevel("ERROR")


    //Definition des sommets
    var myVertices = generateFirstFight().zipWithIndex.map{case (creature, index) => (index.toLong, creature)}
    myVertices.foreach(x=>println("\nID = " + x._1 + ", " + x._2.toString))

    //Definition des arrretes
    var myEdges = generateEdge(myVertices)
    myEdges.foreach(x=>println(x.toString ))
    var myGraph = Graph(sc.makeRDD(myVertices), sc.makeRDD(myEdges))
    //val res = execute(myGraph, 20, sc)





    /*var solar = null
    var warlord = null
    var arrayBarbarian = null
    var arrayOrc = null
    generateFirstFight(solar, arrayBarbarian, arrayOrc, warlord)
    var myVertices = sc.makeRDD(Array(
      (1L, solar),
      (2L, warlord),
      (3L, arrayBarbarian(0)),
      (4L, arrayBarbarian(1)),
      (5L, arrayBarbarian(2)),
      (6L, arrayBarbarian(3)),
      (7L, arrayOrc(0)),
      (8L, arrayOrc(1)),
      (9L, arrayOrc(2)),
      (10L, arrayOrc(3)),
      (11L, arrayOrc(4)),
      (12L, arrayOrc(5)),
      (13L, arrayOrc(6)),
      (14L, arrayOrc(7)),
      (15L, arrayOrc(8)),
      (16L, arrayOrc(9))
    ))

    var myEdges = sc.makeRDD(Array(
      Edge(1L, 2L, "1"),
      Edge(1L, 3L, "2"),
      Edge(1L, 4L, "3"),
      Edge(1L, 5L, "4"),
      Edge(1L, 6L, "5"),
      Edge(1L, 7L, "6"),
      Edge(1L, 8L, "7"),
      Edge(1L, 9L, "8"),
      Edge(1L, 10L, "9"),
      Edge(1L, 11L, "10"),
      Edge(1L, 12L, "11"),
      Edge(1L, 13L, "12"),
      Edge(1L, 14L, "13"),
      Edge(1L, 15L, "14")
    ))*/
  }
}
