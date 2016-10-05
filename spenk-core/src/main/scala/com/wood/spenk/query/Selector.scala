package com.wood.spenk.query

import com.wood.spenk.directory.Fields.Field

case class Selector(field: Field, agg: Option[Aggregate]) {
  def select(line: Array[String]): String = {
    line(field.index)
  }
}
