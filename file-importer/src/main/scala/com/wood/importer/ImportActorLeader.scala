package com.wood.importer

import java.io.File

import akka.actor.Actor
import com.wood.importer.workers.{ColumnMappings, ImportActorPool, WriteLine}

import scala.io.Source
import scala.util.{Failure, Success}

case class FileRequest(filePath: String)

class ImportActorLeader extends Actor {
  override def receive: Receive = {
    case FileRequest(rawPath) =>
      val file = new File(rawPath)
      if (file.exists()) {
        val lines = Source.fromFile(file, "UTF-8").getLines()
        if (lines.hasNext) {
          val line = lines.next().trim
          val columns = line.split('|')
          val mappings = ColumnMappings(
            columns.indexOf("STB"),
            columns.indexOf("TITLE"),
            columns.indexOf("PROVIDER"),
            columns.indexOf("DATE"),
            columns.indexOf("REV"),
            columns.indexOf("VIEW_TIME")
          )
          while (lines.hasNext) {
            val line = lines.next().trim
            if (line.nonEmpty) {
              val split = line.split('|')
              val date = split(mappings.date)

              ImportActorPool.getWorker(date) ! WriteLine(split, mappings)
            }
          }
          sender() ! "Read Complete"
        } else sender() ! Failure(new IllegalArgumentException("File is empty"))
      } else sender() ! Failure(new IllegalArgumentException("File doesn't exist"))
  }


}
