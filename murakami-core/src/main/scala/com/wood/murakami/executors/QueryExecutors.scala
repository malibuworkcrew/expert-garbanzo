package com.wood.murakami.executors

import java.io.File

import com.wood.murakami.Combiner

import scala.io.Source


case class WriteLine(line: Array[String], colMappings: ColumnMappings)
case class ColumnMappings(stb: Int, title: Int, provider: Int, date: Int, rev: Int, viewTime: Int)

// Class responsible for finding duplicates and writing new tracks to file
class QueryExecutors(fileToRead: File, combiner: Combiner) {
  def execute: Combiner = {
    val source = Source.fromFile(fileToRead)
    try {
      val lines = source.getLines()
      while (lines.hasNext) {
        val line = lines.next()
        if (line.nonEmpty) combiner += line
      }
    } finally source.close()
    combiner
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