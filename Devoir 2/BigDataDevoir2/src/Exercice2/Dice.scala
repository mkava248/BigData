package Exercice2

object Dice {

  /**
    * Lance un dé
    *
    * @param range (Int)
    * @return value (Int)
    */
  def roll (range : Int): Int = {
    scala.util.Random.nextInt(range)
  }

}
