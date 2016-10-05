package com.wood.spenk.directory

import java.io.File

import com.wood.spenk.SpenkService

object PathFinder {
  def getPaths(dateFilter: Option[String], fileName: String): Array[File] = {
    val base = new File(SpenkService.baseDir)
    val yearDirs = base.list().filter(_.matches("\\d{4}")).map(p => new File(base + s"/$p"))
    val monthDirs = yearDirs.flatMap(yDir => yDir.list().filter(_.matches("\\d{2}")).map(p => new File(yDir.getPath + s"/$p")))
    monthDirs.flatMap(mDir => mDir.list().filter(_.matches("\\d{2}")).map(p => new File(mDir.getPath + s"/$p/$fileName")))
  }
}
