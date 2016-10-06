package com.wood.murakami.executors

import java.io.File

import com.wood.murakami.Combiner

import scala.io.Source

// Class responsible for reading a single file and sending it into a Combiner
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
}