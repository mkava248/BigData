package Exercice2

class Weapon(val _name:String, val _damage:Array[Int], val _range:Int, val _attackNumber:Int) extends Serializable {

  def attacks(personnage: Personnage): Unit = {
    var r = 1 to _attackNumber
    r foreach (r => print(personnage.addHP(-_damage(r-1))))
  }

  override def toString: String = {
    "\n" + _name + ", Range = " + _range + ", Nombre d'attaque = " + _attackNumber + ", Degats = [" + _damage.mkString(", ") + "]"
  }

}
