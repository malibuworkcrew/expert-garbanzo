package com.wood.murakami.directory

import java.io.File

import com.wood.murakami.MurakamiService

object PathFinder {
  // TODO Respect date filter
  def getPaths(dateFilter: Option[String], fileName: String): Array[File] = {
    val base = new File(MurakamiService.baseDir)
    val yearDirs = base.list().filter(_.matches("\\d{4}")).map(p => new File(base + s"/$p"))
    val monthDirs = yearDirs.flatMap(yDir => yDir.list().filter(_.matches("\\d{2}")).map(p => new File(yDir.getPath + s"/$p")))
    monthDirs.flatMap(mDir => mDir.list().filter(_.matches("\\d{2}")).map(p => new File(mDir.getPath + s"/$p/$fileName")))
  }
}
