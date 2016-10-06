package com.wood.murakami.query

import com.wood.murakami.directory.Fields.{DATE, Field}

// Trait for all our boolean filter logic
trait Expr {
  def check(line: Array[String], date: Boolean = false): Boolean
}

case class Equality(field: Field, value: String) extends Expr {
  override def check(line: Array[String], date: Boolean = false): Boolean = {
    if (date) {
      field != DATE || line.head == value
    } else line(field.index) == value
  }
}

case class AndExpr(left: Expr, right: Expr) extends Expr {
  override def check(line: Array[String], date: Boolean = false): Boolean = left.check(line, date) && right.check(line, date)
}

case class OrExpr(left: Expr, right: Expr) extends Expr {
  override def check(line: Array[String], date: Boolean = false): Boolean = left.check(line, date) || right.check(line, date)
}

case class Filter(expr: Expr) {
  def filter(line: Array[String], date: Boolean = false): Boolean = {
    expr.check(line, date)
  }
}
