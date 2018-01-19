import java.util
import javafx.scene.chart.Chart

/**
  * Created by SANGJIN-NAM on 2018-01-18.
  */
class IndexMaker {

  def getAverageOf_n_Days(chart:util.LinkedList[Candle],n:Int):util.LinkedList[Double]={
    // [i-n] ~ [i]까지 종가의 평균을 구한다.
    var MA_List = new util.LinkedList[Double]
    // 0~(n-1) 까지는 무엇이 입력되어야 하는가.
    for( i <- n to chart.size()-1){
      var sumOfEndPrice:Double = 0
      for( j <- 0 to n){
        sumOfEndPrice += chart.get(i-j).getEndPrice
      }
      var MA = sumOfEndPrice/n
      MA_List.add(MA)
    }
    return MA_List
  }

  //단기가 장기를 상향으로 돌파
  def IsGoldenCross(short:util.LinkedList[Double],long:util.LinkedList[Double]):Boolean={
    for( i <- 1 to short.size-1){
      if(short.get(i-1)<long.get(i-1)){
        if(short.get(i)>long.get(i))return true
      }
    }return false
  }

  //단기가 장기를 하향으로 돌파
  def IsDeadCross(short:util.LinkedList[Double],long:util.LinkedList[Double]):Boolean={
    for( i <- 1 to short.size-1){
      if(short.get(i-1)>long.get(i-1)){
        if(short.get(i)<long.get(i))return true
      }
    }return false
  }

  def getDisparity(chart: util.LinkedList[Candle], MA:util.LinkedList[Double]):util.LinkedList[Double]={
    var disparityList = new util.LinkedList[Double]
    for( i <- 0 to chart.size-1){
      disparityList.add((chart.get(i).getEndPrice / MA.get(i))*100)
    }
    return disparityList
  }





}
