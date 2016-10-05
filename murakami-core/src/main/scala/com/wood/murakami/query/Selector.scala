package com.wood.murakami.query

import com.wood.murakami.directory.Fields.Field

case class Selector(field: Field, agg: Option[Aggregate[_]]) {
  def select(line: Array[String]): String = {
    line(field.index)
  }
}
