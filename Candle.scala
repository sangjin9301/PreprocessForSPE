/**
  * Created by SANGJIN-NAM on 2018-01-18.
  */
class Candle {

  import Direction._

  var time:Double = 0
  var startPrice: Double = 0
  var endPrice: Double = 0
  var highPrice: Double = 0
  var lowPrice: Double = 0
  var direction: Direction = Direction.STREADINESS // enum . 보합
  var Solidity: Double = 0

  def setTime(time:Double):Unit={
    this.time = time
  }
  def getTime:Double={
    return this.time
  }

  def setStartPrice(price:Double):Unit={
    this.startPrice = price
  }
  def getStartPrice:Double={
    return this.startPrice
  }

  def setEndPrice(price:Double):Unit={
    this.endPrice = price
  }
  def getEndPrice:Double={
    return this.endPrice
  }

  def setHighPrice(price:Double):Unit={
    this.highPrice = price
  }
  def getHighPrice:Double={
    return this.highPrice
  }

  def setLowPrice(price:Double):Unit={
    this.lowPrice = price
  }
  def getLowPrice:Double={
    return this.lowPrice
  }

  def setDirection(direction:Direction):Unit={
    this.direction = direction
  }
  def getDirection:Direction={
    return this.direction
  }

  def setSolidity(solidiry:Double):Unit={
    this.Solidity = solidiry
  }
  def getSolidity:Double={
    return this.Solidity
  }
}
