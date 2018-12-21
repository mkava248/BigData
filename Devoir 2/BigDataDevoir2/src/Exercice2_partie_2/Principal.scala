package Exercice2_partie_2

import javax.print.DocFlavor.STRING
import org.apache.spark.graphx._
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ArrayBuffer


object Principal {

  def generateFirstFight(): ArrayBuffer[Personnage] = {

    var tab = new ArrayBuffer[Personnage]()
    //Generation de Solar
    val greatSword = new Weapon("greatSword", Array(35, 30, 25, 20), "3d6+18", 10)
    val slam = new Weapon("slam", Array(30), "2d8+13", 10)
    val longBow = new Weapon("longBow", Array(31, 26, 21, 16), "2d6+14", 110)
    val weaponMap = Array(greatSword, slam, longBow)
    val solar = new Solar("Solar", 363, 44, 15, weaponMap, 50)

    //Generation des 2 Planetares

    //Generation des 2 Movanic Deva
    //Generation des 5 Astral Deva

    //TODO a terminer
    //Generation du grand Wyrm dragon vert
    val breathWeapon = new Weapon("Breath weapon", Array(), "24d6", 70)
    val wyrm = new WyrmDragon("Green Great Wyrm Dragon", 391, 37, Array(breathWeapon), 40)

    //Generation des 10 Angels Slayer
    val arrayOrc = ArrayBuffer[Orc]()
    val doubleAxeAngel = new Weapon("battleAxe", Array(21, 16, 11), "1d8", 10)
    val compositeLongBow = new Weapon("compositeLongBow", Array(19, 14, 9), "1d8", 110)
    (1 to 10) foreach (x => {
      arrayOrc += new AngelSlayer("AngelSlayer_" + x, 112, 26, Array(doubleAxeAngel, compositeLongBow), 40)
    })

    //Generation des 200 barbares orcs
    val arrayBarbarian = ArrayBuffer[Orc]()
    val greatAxe = new Weapon("greatAxe", Array(11), "1d12+10", 10)
    (1 to 200) foreach (x => {
      arrayOrc += new Barbarian("Barbarian_" + x, 42, 15, Array(greatAxe), 0)
    })

    //Generation du warlord
    val viciousFlail = new Weapon("viciousFlail", Array(20, 15, 10), "1d8+10", 10)
    val lionsShield = new Weapon("lionsShield", Array(23), "1d4+6", 10)

    //Affichage
    /*println(solar.toString()+"\n")
    println(arrayOrc.mkString("\n\n"))
    println(arrayBarbarian.mkString("\n\n"))
    println(warlord.toString)*/

    tab += solar
    tab.appendAll(arrayBarbarian)
    tab.appendAll(arrayOrc)
    tab
  }


  //TODO utiliser un map ou un flatmpap à la place du foreach
  def generateEdge(vertices: ArrayBuffer[(Long, Personnage)]): ArrayBuffer[Edge[Int]] = {
    val a = new ArrayBuffer[Edge[Int]]()
    1 to vertices.length - 1 foreach (i => {
      a.append(Edge(vertices(0)._1.toLong, vertices(i)._1.toLong))
      a.append(Edge(vertices(i)._1.toLong, vertices(0)._1.toLong))
    })
    a
  }
/*
  def sendHelp(context: EdgeContext[Personnage, Int, (Personnage, Personnage, String)]): Unit = {
    //    if (context.srcAttr._healPoint < context.srcAttr._healPointMax / 2)
    //      context.srcAttr._messageAllie = "Heal"
    if (!context.dstAttr.isDead() && !context.srcAttr.isDead() /*&& (context.dstAttr._name == "Solar" && context.srcAttr._affilation == true) || (context.srcAttr._name == "Green Great Wyrm Dragon" && context.srcAttr._affilation == false)*/ )
      context.sendToDst((context.dstAttr, context.srcAttr, context.srcAttr._messageAllie))
  }

  def selectTheLowerHP(n: (Personnage, Personnage, Int), m: (Personnage, Personnage, Int, Int)): Unit = {
    if (n._3 < m._3) n
    else m
  }*/

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

        val

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
    val myVertices = generateSecondFight().zipWithIndex.map { case (creature, index) => (index.toLong, creature) }
    myVertices.foreach(x => println("\nID = " + x._1 + ", " + x._2.toString))

    //Definition des arretes
    val myEdges = generateEdge(myVertices)
    myEdges.foreach(x => println(x.toString))
    var myGraph = Graph(sc.makeRDD(myVertices), sc.makeRDD(myEdges))
    /*val res = */ execute(myGraph, 100, sc)

  }
}