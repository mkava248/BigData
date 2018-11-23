package Exercice2

class Solar (_name:String, _healPoint: Int, _armor:Int, val _regeneration:Int, _weaponArray:Array[Weapon])
  extends Personnage(_name, _healPoint, _armor, _weaponArray){

  def attacks(personnage : Personnage, weaponS: String): Unit ={
    val weapon = _weaponArray.lastIndexWhere(weapon => weapon._name==weaponS)
    //personnage.addHP(weapon._)
  }



  override def toString: String = {
    super.toString + ", \nRegeneration = " + _regeneration
  }
}
