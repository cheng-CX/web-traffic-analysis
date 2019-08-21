package com.twq.preparse

import com.twq.{PreParsedLog, WebLogPreParser}
import org.apache.spark.sql.{Encoders, SaveMode, SparkSession}

object PreparseETL {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("PreparseETL")
      .enableHiveSupport()
      .master("local")
      .getOrCreate()

    val rawdataInputPath = spark.conf.get("spark.traffic.analysis.rawdata.input",
    "hdfs://master:9999/user/hadoop-twq/traffic-analysis/rawlog/20180615")

    val numberPartitions = spark.conf.get("spark.traffic.analysis.rawdata.numberPartitions", "2").toInt

    //生成了一个String类型的RDD
    //null变成了none，option的值有可能是Some(x)有可能是none，值在Some(x)里面，这个时候，打平，才能取到Some(x)里面的值
    //如果是none的话，它没有值，直接跳过去了，可以保证没有空指针异常
    val preParsedLogRDD = spark.sparkContext.textFile(rawdataInputPath)
      //.map(line => WebLogPreParser.parse(line)) //返回null报空指针异常
      .flatMap(line => Option(WebLogPreParser.parse(line)))
    //预解析
    val preParsedLogDS = spark.createDataset(preParsedLogRDD)(Encoders.bean(classOf[PreParsedLog]))
    //合并小文件，入库hive
    preParsedLogDS.coalesce(numberPartitions)
      .write
      .mode(SaveMode.Append)
      .partitionBy("year", "month", "day")
      .saveAsTable("rawdata.web")

    spark.stop()
  }
}