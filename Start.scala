/**
  * Created by SANGJIN-NAM on 2018-01-18.
  */
object Start {
  def main(args: Array[String]): Unit = {
    var Input_file_paht = "D:/SPE_DATA/candleChart/A000020.csv"
    var output_dir_path = "D:/SPE_DATA/DATA"
    var output_file_name = "new_A000020"
    new Accessor(Input_file_paht).writeData(output_dir_path, output_file_name)
  }
}
