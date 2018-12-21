package Exercice1

class Spell (var _name : String = "", var _resistance : Boolean) extends Serializable{

  override def toString: String = {
    super.toString
    "name = "+_name+", resitance = "+_resistance
  }
}
