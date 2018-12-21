package Exercice2_partie_2

import scala.math.pow

class Personnage(val _name: String, var _healPoint: Int, val _healPointMax: Int, val _armor: Int, val _weaponArray: Array[Weapon],
                 var _x: Float, var _y: Float, var _speed: Int, var _regeneration : Int = 0, var _cible: Personnage = null,
                 var _distanceCible: Long = 0, var _damage: Int = 0) extends Serializable {

  def isDead(): Boolean = {
    _healPoint <= 0
  }

  def addHP(n: Int): Unit = {
    _healPoint += n
  }

  def move(p: Personnage): Unit = {
    val d = calculateDistance(p)

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

  def calculateDistance(personnage: Personnage): Long = {
    pow(pow(_x - personnage._x, 2) + pow(_y - personnage._y, 2), 0.5).toLong
  }

  def selectWeapon(): Weapon = {
    //selecct weapon
    _healPoint += _regeneration
    if(_healPoint > _healPointMax)
      _healPoint = _healPointMax

    var i = 0
    while (_distanceCible >= _weaponArray(i)._range && i < _weaponArray.length) { // Tant que l'onne peut pas atteindre, on essaye une arme plus grande
      i += 1
      if (i >= _weaponArray.length) { //Cela veut dire que l'on a pas trouve d'arme
        return null
      }
    }
    _weaponArray(i) //retourne la premiere trouvée
  }

  def roll(range: Int): Int = {
    scala.util.Random.nextInt(range + 1) + 1
  }

  def attack(defender: Personnage, weapon: Weapon): Unit = {
    _damage = weapon.attacks(defender)
  }


  override def toString: String = {
    _name + " : PV = " + _healPoint + ", Armure = " + _armor + ", X = " + _x + ", Y = " + _y + ", \nWeapons = " + _weaponArray.mkString(", ")
  }

}