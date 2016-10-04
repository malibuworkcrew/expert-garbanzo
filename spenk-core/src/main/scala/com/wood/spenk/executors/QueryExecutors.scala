package com.wood.spenk.executors

import java.io.File


case class WriteLine(line: Array[String], colMappings: ColumnMappings)
case class ColumnMappings(stb: Int, title: Int, provider: Int, date: Int, rev: Int, viewTime: Int)

// Class responsible for finding duplicates and writing new tracks to file
class QueryExecutors(fileToRead: File) {
  def execute: Unit = {

  }

  def lineOrdered(columnMappings: ColumnMappings, line: Array[String]): String = {
    s"${line(columnMappings.stb)}|" +
      s"${line(columnMappings.title)}|" +
      s"${line(columnMappings.date)}|" +
      s"${line(columnMappings.provider)}|" +
      s"${line(columnMappings.rev)}|" +
      s"${line(columnMappings.viewTime)}"
  }
}