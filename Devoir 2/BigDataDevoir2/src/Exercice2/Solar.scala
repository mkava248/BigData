package Exercice2

class Solar(_name: String, _healPoint: Int, _armor: Int, val _regeneration: Int, _weaponArray: Array[Weapon], _x: Float, _y: Float, _speed: Int)
  extends Personnage(_name, _healPoint, _armor, _weaponArray, _x, _y, _speed) {

 /* def attacks(personnage: Personnage, weaponS: String): Unit = {
    val weapon = _weaponArray.lastIndexWhere(weapon => weapon._name == weaponS)
    //personnage.addHP(weapon._)
  }*/

  override def toString: String = {
    super.toString + ", \nRegeneration = " + _regeneration
  }
}
