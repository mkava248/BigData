import scala.collection.mutable.ArrayBuffer
class Creature (var _name : String = "", var _spells : ArrayBuffer[String] = new ArrayBuffer[String]()) extends Serializable {

  def addSpell(spell : String ) : Unit = {
    _spells += spell
  }

  override def toString: String = {
    super.toString
    "name = "+_name+", spells = "+_spells.toString()
  }
}
