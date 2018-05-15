import java.io.{BufferedReader, File, FileReader, FileWriter}
import java.util

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

  def getData: Array[Array[String]] = {
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

  def toCandle(arr: Array[Array[String]]): util.LinkedList[Candle] = {
    var candle_list = new util.LinkedList[Candle]

    arr.foreach(line => {
      var candle = new Candle
      candle.setTime(line(0))
      candle.setStartPrice(line(1).toDouble)
      candle.setHighPrice(line(2).toDouble)
      candle.setLowPrice(line(3).toDouble)
      candle.setEndPrice(line(4).toDouble)
      candle.setTransactionVolume(line(5).toDouble)
      candle.setTransactionPrice(line(6).toDouble)
      candle.setDirection(line(7).toDouble)
      candle.setDayCompare(line(8).toDouble)
      candle.setSolidity(line(9).toDouble)
      candle_list.add(candle)
    })
    return candle_list
  }

  // todo
  def writeData(out_path:String, fileName:String):Unit={

    val file = new File(out_path)
    if (!file.exists) {
      file.mkdirs()
    }

    val whole_path = out_path + "/" + fileName + ".csv"
    var writer = new FileWriter(whole_path)

    var sb = new StringBuilder
    mkIndex(toCandle(getData)).foreach( day_data =>{
      day_data.foreach( data =>{
        println("...")
        sb.append(data)
        sb.append(",")
      })
      sb.append("\n")
    })

    writer.write(sb.mkString)
    writer.close
  }

  def mkIndex(candle_list:util.LinkedList[Candle]):Array[Array[String]]={
    var maker = new IndexMaker
    //이평선
    var ma_5 = maker.getAverageOf_n_Days(candle_list,5)
    var ma_20 = maker.getAverageOf_n_Days(candle_list,20)
    var ma_60 = maker.getAverageOf_n_Days(candle_list,60)
    var ma_120 = maker.getAverageOf_n_Days(candle_list,120)
    //골든크로스
    var golden_5 = maker.IsGoldenCross(ma_5, ma_20)
    var golden_20 = maker.IsGoldenCross(ma_20, ma_60)
    var golden_60 = maker.IsGoldenCross(ma_60, ma_120)
    //데드크로스
    var dead_5 = maker.IsDeadCross(ma_5, ma_20)
    var dead_20 = maker.IsDeadCross(ma_20, ma_60)
    var dead_60 = maker.IsDeadCross(ma_60, ma_120)
    //일목균형표
    var standardLine = maker.getStandardLine(candle_list)
    var conversionLine = maker.getConversionLine(candle_list)
    var trailingSpan = maker.TrailingSpan(standardLine,conversionLine)
    var leadingSpan_1 = maker.LeadingSpan_1(standardLine,conversionLine)
    var leadingSpan_2 = maker.LeadingSpan_2(candle_list)
    //볼린저밴드
    var bollinger_lower = maker.getBollinger_lower(ma_20)
    var bollinger_upper = maker.getBollinger_upper(ma_20)
    //LRS
    var LRL_list = maker.getLRL(candle_list)
    var LRS_list = maker.getLRS(LRL_list)
    //이격도
    var disparity_5 = maker.getDisparity(candle_list,ma_5)
    var disparity_20 = maker.getDisparity(candle_list,ma_20)
    var disparity_60 = maker.getDisparity(candle_list,ma_60)
    var disparity_120 = maker.getDisparity(candle_list,ma_120)
    //CCI
    var CCI_list = maker.CCI(candle_list)
    //TRIX
    var EMA_1_list = maker.getEMA_1(candle_list)
    var EMA_2_list = maker.getEMA_2(EMA_1_list)
    var EMA_3_list = maker.getEMA_3(EMA_2_list)
    var TRIX_list = maker.getTRIX(EMA_3_list)
    var Sig_list = maker.getTRIX_Sig(TRIX_list)
    //Stochastics
//    var k_list = maker.get_perK(candle_list)
//    var d_list = maker.get_perD(k_list)
//    var slow_k_list = maker.get_Slow_perK(d_list)

    //Reward
    var ROI_list = maker.getRateOfInvestment(candle_list)

    var whole_data = new ArrayBuffer[Array[String]]
    for( i <- 8 to candle_list.size-1 ){
      var data_arr = new ArrayBuffer[String]
//      data_arr+=candle_list.get(i).getTime.toString
      data_arr+=candle_list.get(i).getStartPrice.toString
      data_arr+=candle_list.get(i).getHighPrice.toString
      data_arr+=candle_list.get(i).getLowPrice.toString
      data_arr+=candle_list.get(i).getEndPrice.toString
      data_arr+=candle_list.get(i).getTransactionVolume.toString
      data_arr+=candle_list.get(i).getTransactionPrice.toString
      data_arr+=candle_list.get(i).getDirection.toString
      data_arr+=candle_list.get(i).getDayCompare.toString
      data_arr+=candle_list.get(i).getSolidity.toString
      data_arr+=ma_5.get(i).toString
      data_arr+=ma_20.get(i).toString
      data_arr+=ma_60.get(i).toString
      data_arr+=ma_120.get(i).toString
      data_arr+=golden_5.toString
      data_arr+=golden_20.toString
      data_arr+=golden_60.toString
      data_arr+=dead_5.toString
      data_arr+=dead_20.toString
      data_arr+=dead_60.toString
      data_arr+=standardLine.get(i).toString
      data_arr+=conversionLine.get(i).toString
      data_arr+=trailingSpan.get(i).toString
      data_arr+=leadingSpan_1.get(i).toString
      data_arr+=leadingSpan_2.get(i).toString
      data_arr+=bollinger_lower.get(i).toString
      data_arr+=bollinger_upper.get(i).toString
      data_arr+=LRL_list.get(i)._1.toString
      data_arr+=LRL_list.get(i)._2.toString
      data_arr+=LRS_list.get(i)._1.toString
      data_arr+=LRS_list.get(i)._2.toString
      data_arr+=disparity_5.get(i).toString
      data_arr+=disparity_20.get(i).toString
      data_arr+=disparity_60.get(i).toString
      data_arr+=disparity_120.get(i).toString
      data_arr+=CCI_list.get(i).toString
      data_arr+=EMA_1_list.get(i).toString
      data_arr+=EMA_2_list.get(i).toString
      data_arr+=EMA_3_list.get(i).toString
      data_arr+=TRIX_list.get(i).toString
      data_arr+=Sig_list.get(i).toString
//      data_arr+=k_list.get(i).toString
//      data_arr+=d_list.get(i).toString
//      data_arr+=slow_k_list.get(i).toString
      data_arr+=ROI_list.get(i).toString
      whole_data+=data_arr.toArray
    }
    return whole_data.toArray
  }

}
