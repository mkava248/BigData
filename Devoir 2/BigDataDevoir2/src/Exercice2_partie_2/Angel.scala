package Exercice2_partie_2

class Angel(_name: String, _healPoint: Int, _armor: Int, _regeneration: Int, _weaponArray: Array[Weapon],_speed: Int)
  extends Personnage(_name, _healPoint, _healPoint, _armor, _weaponArray, scala.util.Random.nextInt(400 + 1), scala.util.Random.nextInt(400 + 1), _speed,true, _regeneration) {

  override def toString: String = {
    super.toString + ", \nRegeneration = " + _regeneration
  }
}
