package Exercice2_partie_2

class AngelSlayer (_name: String, _healPoint: Int, _armor: Int, _weaponArray: Array[Weapon], _speed: Int)
  extends Orc(_name, _healPoint, _armor, _weaponArray, scala.util.Random.nextInt(400 + 1), scala.util.Random.nextInt(400 + 1), _speed){

}