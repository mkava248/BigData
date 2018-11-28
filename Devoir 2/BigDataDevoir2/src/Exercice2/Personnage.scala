package Exercice2
import scala.math.pow

class Personnage(val _name:String, var _healPoint: Int, val _armor:Int, val _weaponArray:Array[Weapon],
                 var _x:Float, var _y:Float, var _speed:Int)
  extends Serializable {

  def isDead(): Boolean={
    _healPoint <= 0;
  }

  def addHP(n:Int):Unit={
    _healPoint += n
  }

  def move(p:Personnage): Unit ={
    val d = calculateDistance(p)

    val cosTeta = (p._x - _x)/d
    val sinTeta = (p._y - _y)/d

    if( d - _speed < 5){
      val newSpeed = d - 5
      _x += newSpeed * cosTeta
      _y += newSpeed * sinTeta

    }else{
      _x += _speed * cosTeta
      _y += _speed * sinTeta
    }
  }

  def calculateDistance(personnage: Personnage): Int ={
    pow(pow(_x - personnage._x, 2) + pow(_y - personnage._y, 2), 0.5).toInt
  }

  /*def attack(p:Personnage): Unit ={
    val d = calculateDistance(p)

    val i = 0
    for(i <- 0 until _weaponArray.length){
      if(_weaponArray(i)._range >= d){
        var w = _weaponArray(i)
      }
    }
  }*/

  override def toString: String = {
    _name + " : PV = " + _healPoint + ", Armure = " + _armor + ", \nWeapons = " + _weaponArray.mkString(", ")
  }

}
