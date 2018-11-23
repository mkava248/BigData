package Exercice2
import scala.collection.mutable.ArrayBuffer

object Principal {
  def main(args: Array[String]): Unit = {

    //Generation de Solar
    val greatsWord = new Weapon("greatsWord", Array(35, 30, 25, 20), 10, 5)
    val slam = new Weapon("slam", Array(30), 10, 1)
    val longBow = new Weapon("longBow", Array(31, 26, 21, 16), 10, 5)
    val weaponMap = Array(greatsWord, slam, longBow)
    val solar = new Solar("Solar", 363, 44,15, weaponMap)

    //Generation des worgs rider
    val arrayOrc = ArrayBuffer[Orc]()
    val battleAxe = new  Weapon("battleAxe", Array(2), 2, 2)
    (1 to 9) foreach(x => {arrayOrc += new WorgRider("WordRider_"+x, 13, 18, Array(battleAxe))})

    //Generation des barbares orcs
    val arrayBarbarian = ArrayBuffer[Orc]()
    val doubleAxe = new  Weapon("battleAxe", Array(2), 2, 2)
    val other = new  Weapon("battleAxe", Array(2), 2, 2)
    (1 to 4) foreach(x => {arrayOrc += new WorgRider("Barbarian_"+x, 142, 17, Array(doubleAxe, other))})

    //Generation du warlord
    val viciousFlail = new Weapon("viciousFlail", Array(1), 2, 2)
    val lionsShield = new Weapon("lionsShield", Array(1), 2, 2)
    val warlord = new Warlord("Warlord", 141, 27, Array(viciousFlail, lionsShield))


    println(solar.toString()+"\n")
    println(arrayOrc.mkString("\n\n"))
    println(arrayBarbarian.mkString("\n\n"))
    println(warlord.toString)
  }
}
