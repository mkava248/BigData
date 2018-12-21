package Exercice2

class Barbarian(_name: String, _healPoint: Int, _armor: Int, _weaponArray: Array[Weapon], /*_x:Float, _y:Float,*/ _speed: Int)
  extends Orc(_name, _healPoint, _armor, _weaponArray, /*_x, _y, */ scala.util.Random.nextInt(400 + 1), scala.util.Random.nextInt(400 + 1), _speed) {

}