package com.wood.murakami.query

import com.wood.murakami.directory.Fields.Field

// Simple class for holding our selected columns
case class Selector(field: Field, agg: Option[Aggregate[_]]) {
  def select(line: Array[String]): String = {
    line(field.index)
  }
}
