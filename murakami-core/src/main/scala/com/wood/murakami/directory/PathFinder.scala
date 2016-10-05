package com.wood.murakami.directory

import java.io.File

import com.wood.murakami.MurakamiService
import com.wood.murakami.query.Filter

object PathFinder {
  case class DateFile(date: String, file: File)

  // Track down all directories and filter out those not used
  def getPaths(dateFilter: Option[Filter], fileName: String): Array[File] = {
    val base = new File(MurakamiService.baseDir)
    val yearDirs = checkAndMap("\\d{4}", DateFile("", base))
    val monthDirs = yearDirs.flatMap(yDir => checkAndMap("\\d{2}", yDir))
    // Append file name at end
    val dayDirs = monthDirs.flatMap(mDir => checkAndMap("\\d{2}", mDir))
    dateFilter match {
      case None => dayDirs.map(p => new File(p.file.getPath + s"/$fileName"))
      case Some(dFilter) =>
        dayDirs.filter(dDirs => dFilter.filter(Array(dDirs.date), date = true))
          .map(p => new File(p.file.getPath + s"/$fileName"))
    }
  }

  def checkAndMap(check: String, dFile: DateFile): Array[DateFile] = {
    dFile.file.list().filter(_.matches(check)).map { p =>
      val dStr = if (dFile.date.isEmpty) p else s"${dFile.date}-$p"
      DateFile(dStr, new File(dFile.file.getPath + s"/$p"))
    }
  }
}
