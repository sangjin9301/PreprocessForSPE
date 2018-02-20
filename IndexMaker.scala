import java.sql.Timestamp
import java.util

import org.apache.commons.math3.stat.regression.SimpleRegression

/**
  * Created by SANGJIN-NAM on 2018-01-18.
  */
class IndexMaker {

  //n일 이평 구하기 : [i-n] ~ [i]까지 종가의 평균을 구한다.
  def getAverageOf_n_Days(chart: util.LinkedList[Candle], n: Int): util.LinkedList[Double] = {
    var MA_List = new util.LinkedList[Double]
    var sum: Double = 0

    for (i <- 0 to n - 1) {
      sum += chart.get(i).getEndPrice
      MA_List.add(sum / (i + 1))
    }

    for (i <- n to chart.size() - 1) {
      sum = 0
      for (j <- 0 to n - 1) {
        sum += chart.get(i - j).getEndPrice
      }
      MA_List.add(sum / n)
    }
    return MA_List
  }

  //단기가 장기를 상향으로 돌파
  def IsGoldenCross(short: util.LinkedList[Double], long: util.LinkedList[Double]): Int = {
    for (i <- 1 to short.size - 1) {
      if (short.get(i - 1) < long.get(i - 1)) {
        if (short.get(i) > long.get(i)) return 1
      }
    }
    return 0
  }

  //단기가 장기를 하향으로 돌파
  def IsDeadCross(short: util.LinkedList[Double], long: util.LinkedList[Double]): Int = {
    for (i <- 1 to short.size - 1) {
      if (short.get(i - 1) > long.get(i - 1)) {
        if (short.get(i) < long.get(i)) return 1
      }
    }
    return 0
  }

  def getDisparity(chart: util.LinkedList[Candle], MA: util.LinkedList[Double]): util.LinkedList[Double] = {
    var disparityList = new util.LinkedList[Double]
    for (i <- 0 to chart.size - 1) {
      disparityList.add((chart.get(i).getEndPrice / MA.get(i)) * 100)
    }
    return disparityList
  }

  def getBollinger_upper(MA20_chart: util.LinkedList[Double]): util.LinkedList[Double] = {
    var mean = getMean(MA20_chart)
    var sd = getStandardDeviation(mean, MA20_chart)
    var upper_list = new util.LinkedList[Double]

    MA20_chart.forEach(data => upper_list.add(data + (sd * 2)))
    return upper_list
  }

  def getBollinger_lower(MA20_chart: util.LinkedList[Double]): util.LinkedList[Double] = {
    var mean = getMean(MA20_chart)
    var sd = getStandardDeviation(mean, MA20_chart)
    var upper_list = new util.LinkedList[Double]

    MA20_chart.forEach(data => upper_list.add(data - (sd * 2)))
    return upper_list
  }

  def getLRL(chart: util.LinkedList[Candle]): util.LinkedList[(Double, Double)] = {
    var simpleRegression = new SimpleRegression
    var lrl_list = new util.LinkedList[(Double, Double)]
    chart.forEach(candle => {
      var date = candle.getTime //ex: 20160515
      var year = date.substring(0, 3).toInt
      var month = date.substring(4, 5).toInt
      var day = date.substring(6, 7).toInt
      var ts = new Timestamp(year, month, day, 0, 0, 0, 0)
      simpleRegression.addData(ts.getTime, candle.getEndPrice)
      var w: Double = simpleRegression.getSlope
      var b: Double = simpleRegression.getIntercept
      lrl_list.add((w, b))
    })
    var w: Double = simpleRegression.getSlope
    var b: Double = simpleRegression.getIntercept
    return lrl_list // LRL : ( EndPrice = w *Timestamp + b )
  }

  //todo - list return type
  def getLRS(lrl_list: util.LinkedList[(Double, Double)]): util.LinkedList[(Double, Double)] = {
    var lrs_list = new util.LinkedList[(Double, Double)]
    lrs_list.add((0, 0))
    for (i <- 1 to lrl_list.size - 1) {
      var today_lrl_W = lrl_list.get(i)._1
      var yesterday_lrl_W = lrl_list.get(i - 1)._1
      var today_lrl_bios = lrl_list.get(i)._2
      var yesterday_lrl_bios = lrl_list.get(i - 1)._2
      lrs_list.add(((today_lrl_W + yesterday_lrl_W) / yesterday_lrl_W, (today_lrl_bios + yesterday_lrl_bios) / yesterday_lrl_bios))
    }
    return lrs_list
  } // done : (당일LRL + 전일LRL)/전일LRL

  def getLRS(today_lrl: (Double, Double), yesterday_lrl: (Double, Double)): (Double, Double) = {
    return ((today_lrl._1 + yesterday_lrl._1) / yesterday_lrl._1, (today_lrl._2 + yesterday_lrl._2) / yesterday_lrl._2)
  } // done : (당일LRL + 전일LRL)/전일LRL

  def CCI(chart: util.LinkedList[Candle]): util.LinkedList[Double] = {
    var M_list = new util.LinkedList[Double]
    var m_list = new util.LinkedList[Double]
    var sub_Mm_list = new util.LinkedList[Double]
    var d_list = new util.LinkedList[Double]
    var cci_list = new util.LinkedList[Double]
    var sum: Double = 0

    // M = (고+저+종)/3
    chart.forEach(candle => {
      M_list.add((candle.getHighPrice + candle.getLowPrice + candle.getEndPrice) / 3)
    })

    // m = M의 20일 평균
    for (i <- 0 to 19) {
      sum += M_list.get(i)
      m_list.add(sum / (i + 1))
    }
    sum = 0
    for (i <- 20 to M_list.size - 1) {
      for (j <- (i - 20) to i) {
        sum += M_list.get(j)
      }
      m_list.add(sum / 20)
    }

    // d = |M-m|의 20일 평균
    for (i <- 0 to M_list.size - 1) {
      sub_Mm_list.add(Math.abs(M_list.get(i) - m_list.get(i)))
    }
    sum = 0
    for (i <- 0 to 19) {
      sum += sub_Mm_list.get(i)
      d_list.add(sum / (i + 1))
    }
    sum = 0
    for (i <- 20 to sub_Mm_list.size - 1) {
      for (j <- (i - 20) to i) {
        sum += sub_Mm_list.get(j)
      }
      d_list.add(sum / 20)
    }

    for (i <- 0 to d_list.size - 1) {
      var cii = ((M_list.get(i) - m_list.get(i)) / (d_list.get(i)) * 0.015)
      cci_list.add(cii)
    }
    return cci_list
  }

  def getMean(list: util.LinkedList[Double]): Double = {
    var sum: Double = 0
    list.forEach(data => sum += data)
    return sum / list.size
  }

  def getStandardDeviation(mean: Double, list: util.LinkedList[Double]): Double = {
    var sum: Double = 0
    var diff: Double = 0

    list.forEach(data => {
      diff = data - mean
      sum += diff * diff
    })

    return Math.sqrt(sum / (list.size - 1))
  }

  //전환선
  def getConversionLine(chart: util.LinkedList[Candle]): util.LinkedList[Double] = {
    var conversionLine = new util.LinkedList[Double]
    for (i <- 0 to chart.size - 1) {
      var list_9Days = new util.LinkedList[Double]
      for (j <- 0 to 8) {
        try {
          list_9Days.add(chart.get(i - j).getEndPrice)
        } catch {
          case e: IndexOutOfBoundsException => list_9Days.add(chart.get(i).getEndPrice)
        }
      }
      var max = getMax(list_9Days)
      var min = getMin(list_9Days)
      conversionLine.add((max + min) / 2)
    }
    return conversionLine
  }

  //기준선
  def getStandardLine(chart: util.LinkedList[Candle]): util.LinkedList[Double] = {
    var standardLine = new util.LinkedList[Double]
    for (i <- 0 to chart.size - 1) {
      var list_26Days = new util.LinkedList[Double]
      for (j <- 0 to 25) {
        try {
          list_26Days.add(chart.get(i - j).getEndPrice)
        } catch {
          case e: IndexOutOfBoundsException => list_26Days.add(chart.get(i).getEndPrice)
        }
      }
      var max = getMax(list_26Days)
      var min = getMin(list_26Days)
      standardLine.add((max + min) / 2)
    }
    return standardLine
  }

  def TrailingSpan(standard: util.LinkedList[Double], conversion: util.LinkedList[Double]): util.LinkedList[Double] = {
    var span = new util.LinkedList[Double]
    var trailingSpan = new util.LinkedList[Double]
    for (i <- 0 to standard.size - 1) {
      trailingSpan.add((standard.get(i) + conversion.get(i)) / 2)
    }
    for (i <- 0 to 25) {
      trailingSpan.removeFirst()
      trailingSpan.add(0)
    }
    return trailingSpan
  }

  def LeadingSpan_1(standard: util.LinkedList[Double], conversion: util.LinkedList[Double]): util.LinkedList[Double] = {
    var span = new util.LinkedList[Double]
    var leadingSpan_1 = new util.LinkedList[Double]
    for (i <- 0 to standard.size - 1) {
      span.add((standard.get(i) + conversion.get(i)) / 2)
    }
    for (i <- 0 to 24) {
      leadingSpan_1.add(0)
    }
    for (i <- 0 to span.size - 1) {
      leadingSpan_1.add(span.get(i))
    }
    for (i <- 0 to 24) {
      leadingSpan_1.removeLast
    }
    return leadingSpan_1
  }

  def LeadingSpan_2(chart: util.LinkedList[Candle]): util.LinkedList[Double] = {
    var leadingSpan_2 = new util.LinkedList[Double]
    var span = new util.LinkedList[Double]
    var lowerThan52Index = new util.LinkedList[Double]
    for (i <- 0 to 50) {
      lowerThan52Index.add(chart.get(i).getEndPrice)
      span.add((getMax(lowerThan52Index) + getMin(lowerThan52Index)) / 2)
    }
    for (i <- 51 to chart.size() - 1) {
      var list = new util.LinkedList[Double]
      for (j <- 0 to 51) {
        list.add(chart.get(i - j).getEndPrice)
      }
      span.add((getMax(list) + getMin(list)) / 2)
    }
    for (i <- 0 to 24) {
      leadingSpan_2.add(0)
    }
    for (i <- 0 to span.size - 1) {
      leadingSpan_2.add(span.get(i))
    }
    for (i <- 0 to 24) {
      leadingSpan_2.removeLast
    }
    return leadingSpan_2
  }

  def getMax(list: util.LinkedList[Double]): Double = {
    var max: Double = 0
    list.forEach(x => {
      if (max < x) max = x
    })
    return max
  }

  def getMin(list: util.LinkedList[Double]): Double = {
    var min: Double = 0
    list.forEach(x => {
      if (min < x) min = x
    })
    return min
  }

  def getEMA_1(chart: util.LinkedList[Candle]): util.LinkedList[Double] = {
    var EMA_List = new util.LinkedList[Double]
    var sum: Double = 0

    for (i <- 0 to 24) {
      sum += chart.get(i).getEndPrice
      EMA_List.add(sum / (i + 1))
    }

    for (i <- 25 to chart.size() - 1) {
      sum = 0
      for (j <- 0 to 25) {
        sum += chart.get(i - j).getEndPrice
      }
      EMA_List.add(sum / 25)
    }
    return EMA_List
  }

  def getEMA_2(chart: util.LinkedList[Double]): util.LinkedList[Double] = {
    var EMA_List2 = new util.LinkedList[Double]
    var sum: Double = 0

    for (i <- 0 to 24) {
      sum += chart.get(i)
      EMA_List2.add(sum / (i + 1))
    }

    for (i <- 25 to chart.size() - 1) {
      sum = 0
      for (j <- 0 to 25) {
        sum += chart.get(i - j)
      }
      EMA_List2.add(sum / 25)
    }
    return EMA_List2
  }

  def getEMA_3(chart: util.LinkedList[Double]): util.LinkedList[Double] = {
    var EMA_List3 = new util.LinkedList[Double]
    var sum: Double = 0

    for (i <- 0 to 24) {
      sum += chart.get(i)
      EMA_List3.add(sum / (i + 1))
    }

    for (i <- 25 to chart.size() - 1) {
      sum = 0
      for (j <- 0 to 25) {
        sum += chart.get(i - j)
      }
      EMA_List3.add(sum / 25)
    }
    return EMA_List3
  }

  def getTRIX(chart: util.LinkedList[Double]): util.LinkedList[Double] = {
    var trix_list = new util.LinkedList[Double]
    trix_list.add(0)
    for (i <- 1 to chart.size - 1) {
      var preEMA = chart.get(i - 1)
      var EMA = chart.get(i)
      trix_list.add((EMA - preEMA) / preEMA)
    }
    return trix_list
  }

  def getTRIX_Sig(chart: util.LinkedList[Double]): util.LinkedList[Double] = {
    var sig_list = new util.LinkedList[Double]
    var sum: Double = 0

    for (i <- 0 to 8) {
      sum += chart.get(i)
      sig_list.add(sum / (i + 1))
    }

    for (i <- 9 to chart.size() - 1) {
      sum = 0
      for (j <- 0 to 8) {
        sum += chart.get(i - j)
      }
      sig_list.add(sum / 9)
    }
    return sig_list
  }

  def get_perK(chart: util.LinkedList[Candle]): util.LinkedList[Double] = {
    var k_list = new util.LinkedList[Double]

    for (i <- 0 to chart.size - 1) {
      var today_EndPrice = chart.get(i).getEndPrice
      var Min: Double = 0
      var Max: Double = 0
      if (i < 13) {
        var list = new util.LinkedList[Double]
        list.add(chart.get(i).getEndPrice)
        Min = getMin(list)
        Max = getMax(list)
        k_list.add((today_EndPrice - Min) / (Max - Min))

      } else {
        var list_of14day = new util.LinkedList[Double]
        for (j <- 0 to 12) {
          list_of14day.add(chart.get(i - j).getEndPrice)
        }
        Max = getMax(list_of14day)
        Min = getMin(list_of14day)
        k_list.add((today_EndPrice - Min) / (Max - Min))
      }
    }
    return k_list
  }

  def get_perD(chart: util.LinkedList[Double]): util.LinkedList[Double] = {
    var d_list = new util.LinkedList[Double]
    var sum: Double = 0
    for (i <- 0 to 2) {
      sum += chart.get(i)
      d_list.add(sum / (i + 1))
    }
    for (i <- 3 to chart.size - 1) {
      sum = 0
      for (j <- 0 to 2) {
        sum += chart.get(i - j)
      }
      d_list.add(sum / 3)
    }
    return d_list
  }

  def get_Slow_perK(chart: util.LinkedList[Double]): util.LinkedList[Double] = {
    var slowD_list = new util.LinkedList[Double]
    var sum: Double = 0
    for (i <- 0 to 2) {
      sum += chart.get(i)
      slowD_list.add(sum / (i + 1))
    }
    for (i <- 3 to chart.size - 1) {
      sum = 0
      for (j <- 0 to 2) {
        sum += chart.get(i - j)
      }
      slowD_list.add(sum / 3)
    }
    return slowD_list
  }

  def getRateOfInvestment(chart:util.LinkedList[Candle]):util.LinkedList[Double]={
    var ROI_list = new util.LinkedList[Double]
    for(i <- 1 until chart.size){
      ROI_list.add((chart.get(i).getEndPrice-chart.get(i-1).getEndPrice)/(chart.get(i-1).getEndPrice)*100)
    }
    ROI_list.add(0)
    return ROI_list
  }


}
