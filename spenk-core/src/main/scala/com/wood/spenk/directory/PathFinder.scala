package com.wood.spenk.directory

import java.io.File

import com.wood.spenk.SpenkService

object PathFinder {
  def getPaths(dateFilter: Option[String]): Array[File] = {
    val base = new File(SpenkService.baseDir)
    val yearDirs = base.list().filter(_.matches("/^\d{4}$/")).map(new File(_))
    val monthDirs = yearDirs.flatMap(_.list().filter(_.matches("/^\d{2}$/"))).map(new File(_))
    monthDirs.flatMap(_.list().filter(_.matches("/^\d{2}$/"))).map(new File(_))
  }
}
