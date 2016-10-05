package com.wood.murakami.query

import com.wood.murakami.directory.Fields.Field

case class Selector(field: Field, agg: Option[Aggregate]) {
  def select(line: Array[String]): String = {
    line(field.index)
  }
}
