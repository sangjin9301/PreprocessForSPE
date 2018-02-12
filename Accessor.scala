import java.io.{BufferedReader, File, FileReader, FileWriter}

import scala.collection.mutable.ArrayBuffer
import org.apache.commons.io.LineIterator
/**
  * Created by SANGJIN-NAM on 2018-02-12
  */


/*
 * todo
 * 1. 파일읽기
 * 2. 파라미터로 candle 생성하기
 * 3. candleChart 만들기
 *
 */
class Accessor(path:String) {

  val splitBy = ","

  def getData(path: String): Array[Array[String]] = {
    var arr = new ArrayBuffer[Array[String]]
    var bufReader: BufferedReader = null
    bufReader = new BufferedReader(new FileReader(path))
    val iter = new LineIterator(bufReader)

    while (iter.hasNext) {
      arr += iter.nextLine().split(splitBy)
    }
    arr.remove(0)
    return arr.toArray
  }

  def toCandle(arr: Array[Array[String]]): Array[Candle] = {
    var candle_list = new ArrayBuffer[Candle]

    //todo Implements get/set method

    arr.foreach(line => {
      var candle = new Candle
      candle.setTime(line(0))
      candle.setStartPrice(line(1))
      candle.setHighPrice(line(2).toDouble)
      candle.setLowPrice(line(3).toInt)
      candle.setEndPrice(line(4).toInt)
      //거래량 candle.set(line(5))
      //거래대금 candle.set(line(6))
      candle.setDirection(line(7))
      //전일대비
      candle.setSolidity(line(9))
      candle_list += candle
    })
    return candle_list.toArray
  }



}
