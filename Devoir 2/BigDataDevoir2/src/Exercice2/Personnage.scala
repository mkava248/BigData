package Exercice2

class Personnage(val _name:String, var _healPoint: Int, val _armor:Int, val _weaponArray:Array[Weapon]) {

  def isDead(): Boolean={
    return _healPoint <= 0;
  }

  def addHP(n:Int):Unit={
    _healPoint += n
  }

  override def toString: String = {
    _name + " : PV = " + _healPoint + ", Armure = " + _armor + ", \nWeapons = " + _weaponArray.mkString(", ")
  }

}
