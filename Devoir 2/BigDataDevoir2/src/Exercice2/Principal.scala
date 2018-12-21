package Exercice2

import org.apache.spark.graphx._
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ArrayBuffer

//tiebreak value is generated randomly before every graph iteration
class node(val id: Int, val color: Int = 1, val knighthood: Boolean = false, val tiebreakValue: Long = 1L) extends Serializable {
  override def toString: String = s"id : $id tiebreakValue : $tiebreakValue color : $color knighthood : $knighthood"
}


object Principal {

  def generateFirstFight(): ArrayBuffer[Personnage] = {

    var tab = new ArrayBuffer[Personnage]()
    //Generation de Solar
    val greatSword = new Weapon("greatSword", Array(35, 30, 25, 20), "3d6+18", 10)
    val slam = new Weapon("slam", Array(30), "2d8+13", 10)
    val longBow = new Weapon("longBow", Array(31, 26, 21, 16), "2d6+14", 110)
    val weaponMap = Array(greatSword, slam, longBow)
    val solar = new Solar("Solar", 363, 44, 15, weaponMap, 0, 0, 50)

    //Generation des worgs rider
    val arrayOrc = ArrayBuffer[Orc]()
    val battleAxe = new Weapon("battleAxe", Array(2), "1d8+2", 10)
    (1 to 9) foreach (x => {
      arrayOrc += new WorgRider("WorgRider_" + x, 13, 18, Array(battleAxe), 5, 5, 10)
    })

    //Generation des barbares orcs
    val arrayBarbarian = ArrayBuffer[Orc]()
    val doubleAxe = new Weapon("doubleAxe", Array(19, 14, 9), "1d8+10", 10)
    (1 to 4) foreach (x => {
      arrayOrc += new WorgRider("Barbarian_" + x, 142, 17, Array(doubleAxe), 10, 10, 10)
    })

    //Generation du warlord
    val viciousFlail = new Weapon("viciousFlail", Array(20, 15, 10), "1d8+10", 10)
    val lionsShield = new Weapon("lionsShield", Array(23), "1d4+6", 10)
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
  def generateEdge(vertices: ArrayBuffer[(Long, Personnage)]): ArrayBuffer[Edge[Int]] = {
    val a = new ArrayBuffer[Edge[Int]]()
    1 to vertices.length - 1 foreach (i => {
      //val distance = vertices(0)._2.calculateDistance(vertices(i)._2)
      a.append(Edge(vertices(0)._1.toLong, vertices(i)._1.toLong /*, distance*/))
      a.append(Edge(vertices(i)._1.toLong, vertices(0)._1.toLong /*, distance*/))
    })
    a
  }

  //(ID du personnage source, (le personnage destination, son ID, la distance))
  def sendPosition(context: EdgeContext[Personnage, Int, (Personnage, Personnage, Long)]): Unit = {
    //            println("Source : " + context.srcAttr._name)
    //            println("Destination : " + context.dstAttr._name)
    //            println("Distance : " + context.attr.toString)
    //            print("\n\n")
    //    println(context.srcId)
    //    context.sendToSrc((context.dstAttr, context.dstId.toInt, context.attr))
    if (!context.dstAttr.isDead() && !context.srcAttr.isDead())
      context.sendToDst((context.dstAttr, context.srcAttr, context.dstAttr.calculateDistance(context.srcAttr)))
  }


  def selectTheClosest(n: (Personnage, Personnage, Long), m: (Personnage, Personnage, Long)): (Personnage, Personnage, Long) = {
    //retourne le minimum dans cet exemple
    //        print(n)
    //        print(" ")
    //        println(m)
    if (n._3 < m._3) n
    else m
  }


  //(ID du personnage source, (le personnage destination, son ID, les degats))
  def sendDamage(context: EdgeContext[Personnage, Int, (Personnage, Int)]): Unit = {
    if (context.srcAttr._cible._name.equals(context.dstAttr._name) && (!context.dstAttr.isDead() && !context.srcAttr.isDead()))
      context.sendToDst((context.dstAttr, context.srcAttr._damage))
  }


  def execute(myGraph: Graph[Personnage, Int], maxIterations: Int, context: SparkContext): Unit = {
    var counter = 0
    val fields = new TripletFields(true, false, false) //join strategy

    def loop1: Unit = {
      var graph2 = myGraph
      while (true) {

        counter += 1
        if (counter >= maxIterations) return
        println("ITERATION NUMERO : " + counter)

        val messages = graph2.aggregateMessages[(Personnage, Personnage, Long)](
          sendPosition,
          selectTheClosest
          //fields //use an optimized join strategy (we don't need the edge attribute)
        )

        //        messages foreach (x => println("(ID vertex : " + x._1 + ", " + "(Vertex Source : " + x._2._1._name + ", PV : " + x._2._1._healPoint + ", Dest : " + x._2._2._name + ", distance = " + x._2._3.toInt + "))"))

        //        messages foreach (x => println(x._1.getClass + " " + x._1.toString + ", " + x._2._1.getClass + ", " + x._2._2.getClass + ", " + x._2._3.getClass))

        if (messages.isEmpty()) return //condition d'arret
        //        println("*******")

        //Le message envoyé est x._2
        //VertextID du chaque sommet du graph, ID de chaque message (x._1)
        graph2 = graph2.joinVertices(messages) {
          (vertexID, pSrc, msgrecu) => {
            //            println("ID = " + vertexID.toString)
            //            println("source :" + pSrc._name)
            //            println("source : " + msgrecu._1._name + ", dest = " + msgrecu._2._name + ", distance = " + msgrecu._3)

            msgrecu._1._cible = msgrecu._2
            msgrecu._1._distanceCible = msgrecu._3

            val weapon = msgrecu._1.selectWeapon()
            if (weapon == null) {
              //Le monstre n'a pas assez de portée, il avance
              msgrecu._1.move(msgrecu._2)
              //              println("A bougé" + "\n")
            } else {
              msgrecu._1.attack(msgrecu._2, weapon)
              //              println("Dégat fait : " + msgrecu._1._damage + "\n")
            }

            msgrecu._1
          }
        }
        //        println("***********************")

        //        graph2.vertices.collect() foreach (x => println(x._1 + ", " + x._2._name + ", " + x._2._healPoint))

        //        println("\n\n*** Affichage du merge des messages ***")
        val messageDamage = graph2.aggregateMessages[(Personnage, Int)](
          sendDamage,
          (a, b) => {
            (a._1, a._2 + b._2)
          }
        )
        //        println("\n\n*** Affichage du messages ***")
        //messageDamage foreach (x => println("(ID vertex : " + x._1 + ", " + "(Vertex Destinataire : " + x._2._1._name + ", PV : " + x._2._1._healPoint + ", degats : " + x._2._2 + "))"))

        graph2 = graph2.joinVertices(messageDamage) {
          (VertexID, psrc, msgrecu) => {
            msgrecu._1.addHP(-msgrecu._2)
            msgrecu._1
          }
        }


        graph2.vertices.collect() foreach (x => println(x._1 + ", " + x._2._name + ", PV = " + x._2._healPoint + ", [" + x._2._x + ", " + x._2._y + "]" + ", Cible [" + x._2._cible._name + ", " + x._2._distanceCible + ", " + x._2._damage + "]\n"))
        println("*** Fin de l'iteration " + counter + "***\n\n\n")
        //Reconstruire un graphe à partir des vertices modifiés
        /*myGraph=myGraph.fromyGraph.vertices)
myGraph.edges=context.makeRDD(newEdges)
        myGraph = Graph(context.makeRDD(myGraph.vertices), context.makeRDD(newEdges))*/


        //        println(myGraph.edges)

        //        messages.map(x => if(x._1.(x._2._2.toInt, x._1)).reduceByKey(_+_) foreach println
        //        messages foreach (x => println("(ID vertex : " + x._1 + ", " + "(Vertex destination : " + x._2._1._name + ", son ID " + x._2._2 + ", distance = " + x._2._3.toInt + "))"))


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


  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("Petersen Graph (10 nodes)").setMaster("local[*]")
    val sc = new SparkContext(conf)
    sc.setLogLevel("ERROR")


    //Definition des sommets
    val myVertices = generateFirstFight().zipWithIndex.map { case (creature, index) => (index.toLong, creature) }
    myVertices.foreach(x => println("\nID = " + x._1 + ", " + x._2.toString))

    //Definition des arretes
    val myEdges = generateEdge(myVertices)
    myEdges.foreach(x => println(x.toString))
    var myGraph = Graph(sc.makeRDD(myVertices), sc.makeRDD(myEdges))
    /*val res = */ execute(myGraph, 100, sc)

  }
}