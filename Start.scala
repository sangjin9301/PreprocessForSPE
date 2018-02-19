import java.io.{BufferedWriter, File, FileOutputStream, OutputStreamWriter}
import java.util

/**
  * Created by SANGJIN-NAM on 2018-01-18.
  */
object Start {
  def main(args: Array[String]): Unit = {

    var input_file_path = "D:/SPE_DATA/candleChart/"
    var output_dir_path = "D:/SPE_DATA/DATA"
    var output_file_name = "new_A000020"
    var file_list = new util.LinkedList[String]

    var dir = new File(input_file_path)
    if(dir.exists() == false) {
      println("입력 디렉토리 경로가 존재하지 않습니다.")
      return;
    }

    visitAllFiles(file_list, dir)

    file_list.forEach(fileName => {
      new Accessor(input_file_path+fileName).writeData(output_dir_path, "new_"+fileName)
    })

  }

  def visitAllFiles(files:util.LinkedList[String], dir:File):Unit={
    if(dir.isDirectory){
      var children = dir.listFiles
      children.foreach(file => {visitAllFiles(files, file)})
    }
    else{
      files.add(dir.getName)
      println(dir.getName)
    }
  }
}
