package com.wood.importer.workers

import java.io.{File, FileOutputStream, FileWriter, PrintWriter}
import java.nio.file.{Files, StandardCopyOption}

import akka.actor.Actor

import scala.io.Source


case class WriteLine(line: Array[String], colMappings: ColumnMappings)
case class ColumnMappings(stb: Int, title: Int, provider: Int, date: Int, rev: Int, viewTime: Int)

// Class responsible for finding duplicates and writing new tracks to file
class ImportActorWorkers extends Actor {
  val fileName = "formatted.psv"

  override def receive: Actor.Receive = {
    case WriteLine(line, colMappings) =>
      val date = line(colMappings.date)
      val dateSplit = date.split("-")
      val file = new File(s"data/${dateSplit(0)}/${dateSplit(1)}/${dateSplit(2)}/$fileName")
      file.getParentFile.mkdirs()
      if (file.exists()) {
        val source = Source.fromFile(file, "UTF-8")
        val fileLines = source.getLines()
        var duplicateFound = false
        var dupeIndex = -1
        var exactMatch = false

        try {
          // Performance Note:: Approach assumes duplicates more rare than uniques
          // Duplicate string to search for
          val searchString = s"${line(colMappings.stb)}|${line(colMappings.title)}|${line(colMappings.date)}"
          while (fileLines.hasNext && !duplicateFound) {
            val fileLine = fileLines.next()
            duplicateFound = fileLine.startsWith(searchString)
            if (duplicateFound) exactMatch = fileLine == lineOrdered(colMappings, line)
            dupeIndex += 1
          }
        } finally source.close()
        // If duplicate found, create new file without it, otherwise just write next line
        if (duplicateFound && !exactMatch) {
          rewriteFileWithoutDupe(dupeIndex, lineOrdered(colMappings, line), file)
        } else if (!exactMatch) {
          write(lineOrdered(colMappings, line), file)
        }
      } else {
        // Create a new file and add our row
        file.createNewFile()
        write(lineOrdered(colMappings, line), file)
      }
  }

  def rewriteFileWithoutDupe(dupeIndex: Int, line: String, file: File): Unit = {
    val source = Source.fromFile(file)
    val lines = source.getLines()
    val outputFile = File.createTempFile("writeTmp", ".psv", file.getParentFile)
    val writer = new FileWriter(outputFile)
    try {
      var index = 0
      while (lines.hasNext) {
        val line = lines.next()
        if (index != dupeIndex) writer.write(line + "\n")
        index += 1
      }
      writer.write(line)
    } finally {
      writer.close()
      source.close()
    }
    // Using REPLACE_EXISTING and ATOMIC_MOVE will keep the query tool from blowing up with a read
    Files.move(outputFile.toPath, file.toPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
  }

  def write(line: String, file: File): Unit = {
    val writer = new PrintWriter(new FileOutputStream(file,true))
    try {
      writer.append(line + "\n")
    } finally writer.close()
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