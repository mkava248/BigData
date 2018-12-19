package Exercice2

import scala.math.pow

class Personnage(val _name: String, var _healPoint: Int, val _armor: Int, val _weaponArray: Array[Weapon],
                 var _x: Float, var _y: Float, var _speed: Int, var _cible: Personnage = null,
                 var _distanceCible: Long = 0, var _damage: Int = 0) extends Serializable {

  def isDead (): Boolean = {
  _healPoint <= 0;
}

  def addHP (n: Int): Unit = {
  _healPoint += n
}

  def move (p: Personnage): Unit = {
  val d = calculateDistance (p)

  val cosTeta = (p._x - _x) / d
  val sinTeta = (p._y - _y) / d

  if (d - _speed < 5) {
  val newSpeed = d - 5
  _x += newSpeed * cosTeta
  _y += newSpeed * sinTeta

} else {
  _x += _speed * cosTeta
  _y += _speed * sinTeta
}
}

  def calculateDistance (personnage: Personnage): Int = {
  pow (pow (_x - personnage._x, 2) + pow (_y - personnage._y, 2), 0.5).toInt
}

  def selectWeapon (distance: Long): Weapon = {
  //selecct weapon
  var i = 0
  while (distance > _weaponArray (i)._range && i < _weaponArray.length) {
  i += 1
  if (i >= _weaponArray.length) {
  return null
}
}
  _weaponArray (i)
}

  def roll (range: Int): Int = {
  scala.util.Random.nextInt (range + 1) + 1
}

  def attack (defenser: Personnage, weapon: Weapon): Int = {
  var i = 0
  val hpDefenseur = defenser._healPoint
  var damage = 0
  while (i < weapon._damage.length && hpDefenseur - damage > 0) {
  if (roll (20) + weapon._damage (i) >= defenser._armor) {
  //        damage += 3 * roll(6) + 18
  damage += 1
}
  i += 1
}
  damage
}


  override def toString: String = {
  _name + " : PV = " + _healPoint + ", Armure = " + _armor + ", X = " + _x + ", Y = " + _y + ", \nWeapons = " + _weaponArray.mkString (", ")
}

}
