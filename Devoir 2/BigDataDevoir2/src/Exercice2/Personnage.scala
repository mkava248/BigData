package Exercice2
import math.pow

class Personnage(val _name:String, var _healPoint: Int, val _armor:Int, val _weaponArray:Array[Weapon], var _x:Float, var _y:Float)
  extends Serializable {

  def isDead(): Boolean={
    return _healPoint <= 0;
  }

  def addHP(n:Int):Unit={
    _healPoint += n
  }

  def calculateDistance(personnage: Personnage): Int ={
    pow(pow(_x - personnage._x, 2) + pow(_y - personnage._y, 2), 0.5).toInt
  }

  override def toString: String = {
    _name + " : PV = " + _healPoint + ", Armure = " + _armor + ", \nWeapons = " + _weaponArray.mkString(", ")
  }

}
