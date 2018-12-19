package Exercice2

import org.apache.spark.graphx._
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ArrayBuffer

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
  def generateFirstFight(): ArrayBuffer[Personnage] = {

    var tab = new ArrayBuffer[Personnage]()
    //Generation de Solar
    val greatsWord = new Weapon("greatsWord", Array(35, 30, 25, 20), 10, 5)
    val slam = new Weapon("slam", Array(30), 10, 1)
    val longBow = new Weapon("longBow", Array(31, 26, 21, 16), 10, 5)
    val weaponMap = Array(greatsWord, slam, longBow)
    val solar = new Solar("Solar", 363, 44, 15, weaponMap, 0, 0, 0)

    //Generation des worgs rider
    val arrayOrc = ArrayBuffer[Orc]()
    val battleAxe = new Weapon("battleAxe", Array(2), 2, 2)
    (1 to 9) foreach (x => {
      arrayOrc += new WorgRider("WordRider_" + x, 13, 18, Array(battleAxe), 5, 5, 10)
    })

    //Generation des barbares orcs
    val arrayBarbarian = ArrayBuffer[Orc]()
    val doubleAxe = new Weapon("battleAxe", Array(2), 2, 2)
    val other = new Weapon("battleAxe", Array(2), 2, 2)
    (1 to 4) foreach (x => {
      arrayOrc += new WorgRider("Barbarian_" + x, 142, 17, Array(doubleAxe, other), 10, 10, 10)
    })

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
  def generateEdge(vertices: ArrayBuffer[(Long, Personnage)]): ArrayBuffer[Edge[Int]] = {
    val a = new ArrayBuffer[Edge[Int]]()
    1 to vertices.length - 1 foreach (i => {
      val distance = vertices(0)._2.calculateDistance(vertices(i)._2)
      a.append(Edge(vertices(0)._1.toLong, vertices(i)._1.toLong, distance))
      a.append(Edge(vertices(i)._1.toLong, vertices(0)._1.toLong, distance))
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
    context.sendToDst((context.dstAttr, context.srcAttr, context.attr))
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
    context.sendToDst((context.dstAttr, context.attr))
  }


  def execute(myGraph: Graph[Personnage, Int], maxIterations: Int, context: SparkContext): Unit = {
    var counter = 0
    val fields = new TripletFields(true, false, false) //join strategy

    def loop1: Unit = {
      while (true) {

        counter += 1
        println("ITERATION NUMERO : " + counter)
        if (counter >= maxIterations) return

        val messages = myGraph.aggregateMessages[(Personnage, Personnage, Long)](
          sendPosition,
          selectTheClosest
          //fields //use an optimized join strategy (we don't need the edge attribute)
        )
        messages foreach (x => println("(ID vertex : " + x._1 + ", " + "(Vertex Source : " + x._2._1._name + ", PV : " + x._2._1._healPoint + ", Dest : " + x._2._2._name + ", distance = " + x._2._3.toInt + "))"))
        //                messages foreach println
        //        messages foreach (x => println(x._1.getClass + " " + x._1.toString + ", " + x._2._1.getClass + ", " + x._2._2.getClass + ", " + x._2._3.getClass))
        if (messages.isEmpty()) return
        println("*******1")
        //Le message envoyé est x._2
        //VertextID du chaque sommet du graph, ID de chaque message (x._1)
        val newGraph = myGraph.joinVertices(messages) {
          (vertexID, pSrc, msgrecu) => {
            println("ID = " + vertexID.toString)
            println("source :" + pSrc._name)
            println("source : " + msgrecu._1._name + ", dest = " + msgrecu._2._name + ", distance = " + msgrecu._3 + "\n")

            //write code _1 attaque _2
//            msgrecu._1.addHP(-1);
//            msgrecu._1.addHP(-1);
            msgrecu._1._cible = msgrecu._2
            msgrecu._1._distanceCible = msgrecu._3


            msgrecu._1._damage = // TODO

            /*val weapon = msgrecu._1.selectWeapon(msgrecu._3)
            if (weapon == null) {
              //Le monstre n'a pas assez de portée, il avance
              msgrecu._1._ennemy = msgrecu._2
              msgrecu._1.distanceEnnemy = msgrecu._3
              //msgrecu._1.move(msgrecu._2)
            } else {
              //msgrecu._1.attack(msgrecu._2, weapon)
            }*/


              msgrecu._1




            //          val attaquant = dist._1
            //          val defenseur = dist._2
            //          var attribut = dist._3 //distance
            //          //          println("A : " + attaquant._name + ", D : " + defenseur._name + ", att : " + attribut)
            //          val weapon = attaquant.selectWeapon(attribut)
            //
            //          //Cela veut dire que l'attaquant est trop loin
            //          if (weapon == null) {
            //            attaquant.move(defenseur)
            //            attribut = 0 //damage
            //          } else {
            //            //l'attaquant est assez près
            //            attribut = attaquant.attack(defenseur, weapon) //damage
            //          }
            //          attaquant.addHP(-attribut.toInt) //retirer des PV
            //          attaquant
          }
        }
        println("***********************")
        val b = newGraph.vertices.collect()
        b foreach (x => println(x._1 + ", " + x._2._name + ", " + x._2._healPoint))
        /*      b foreach (a => println(a._1, a._2._name, a._2._healPoint))

                   println("********")
                   messages foreach (m => {
                     val attaquant = m._2._1
                     val defenseur = m._2._2
                     var attribut = m._2._3 //distance
                     println("A : " + attaquant._name + ", D : " + defenseur._name + ", att : " + attribut)
                     val weapon = attaquant.selectWeapon(defenseur, attribut)

                     if (weapon == null) {
                       attaquant.move(defenseur)
                       attribut = 0
                     } else {
                       attribut = attaquant.attack(defenseur, weapon) //damage
                     }
                     println("***A : " + attaquant._name + ", D : " + defenseur._name + ", att : " + attribut + "\n")

                   })
                   messages foreach (x => println("(ID vertex : " + x._1 + ", " + "(Vertex Source : " + x._2._1._name + ", Dest " + x._2._2._name + ", distance = " + x._2._3.toInt + "))"))


                   val messages2 = myGraph.aggregateMessages[(Personnage, Int)](
                     sendDamage,
                     { (x, y) => (x._1, x._2 + y._2) }
                     //fields //use an optimized join strategy (we don't need the edge attribute)
                   )
                   //        messages2 foreach (x => println("(ID vertex : " + x._1 + ", " + "(attaquant : " + x._2._1._name + ", degats : " + x._2._2 + "))"))

           */
        /*messages2 foreach (m => {
          val attaquant = m._2._1
          val defenseur = m._2._2
          var attribut = m._2._3 //distance
          val weapon = attaquant.selectWeapon(defenseur, attribut)
          if (weapon == null) {
            attaquant.move(defenseur)
            attribut = 0
          } else {
            attribut = attaquant.attack(defenseur, weapon) //damage
          }
        })*/


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
    val myGraph = Graph(sc.makeRDD(myVertices), sc.makeRDD(myEdges))
    /*val res = */ execute(myGraph, 2, sc)

  }
}