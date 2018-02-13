/**
  * Created by SANGJIN-NAM on 2018-01-18.
  */
class Candle {

  var time:Double = 0
  var startPrice: Double = 0
  var endPrice: Double = 0
  var highPrice: Double = 0
  var lowPrice: Double = 0
  var direction: Double = 0
  var solidity: Double = 0
  var volume:Double = 0
  var transactionPrice:Double = 0
  var dayCompare:Double = 0

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

  def setDirection(direction:Double):Unit={
    this.direction = direction
  }
  def getDirection:Double={
    return this.direction
  }

  def setSolidity(solidiry:Double):Unit={
    this.solidity = solidiry
  }
  def getSolidity:Double={
    return this.solidity
  }

  def setTransactionVolume(vol:Double):Unit={
    this.volume = vol
  }
  def getTransactionVolume:Double={
    return this.volume
  }

  def setTransactionPrice(tran_P:Double):Unit={
    this.transactionPrice = tran_P
  }
  def getTransactionPrice:Double={
    return this.transactionPrice
  }

  def setDayCompare(day_C:Double):Unit={
    this.dayCompare = day_C
  }
  def getDayCompare:Double={
    return this.dayCompare
  }
}
