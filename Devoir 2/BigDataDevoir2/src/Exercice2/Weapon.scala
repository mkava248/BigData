package Exercice2

class Weapon(val _name:String, val _touch:Array[Int], val _damage: String, val _range:Int) extends Serializable {

  def attacks(defender: Personnage): Int = {
    var i = 0
    var damages = 0
    val hpDefenseur = defender._healPoint
    while (i < _touch.length && hpDefenseur - damages > 0) {
      var randomTouch = roll(20)
      if(randomTouch == 20){ //réussite critique, on touche obligatoirement
        damages += this.damage()
      }
      else if((randomTouch + _touch(i)) >= defender._armor){//On regarde si on touche
        damages += this.damage()
      }
      i+=1
    }
    damages
  }

  override def toString: String = {
    "\n" + _name + ", Range = " + _range + ", Nombre d'attaque = " + _touch.length + ", Pour toucher = [" + _touch.mkString(", ") + "], Dégât = "+ _damage
  }

  def roll (range: Int): Int = {
    scala.util.Random.nextInt (range + 1) + 1
  }

  //_damage doit prendre la forme (3d6+18)
  //Permet de convertir la phrase en dégat a faire
  def damage(): Int = {
    var dec = _damage.split("\\+")
    var dice = dec(0).split("d")
    var nomberDice = dice(0).toInt
    var valueDice = dice(1).toInt
    var constant = dec(1).toInt

    var damages = nomberDice * roll(valueDice) + constant
    damages
  }

}