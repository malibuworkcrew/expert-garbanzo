package com.wood.murakami.directory

import java.text.SimpleDateFormat

// Holder for field specific functionality
object Fields {
  val hourFormat = new SimpleDateFormat("HH:mm")
  val dayFormat = new SimpleDateFormat("yyyy-MM-dd")
  val fields = Seq(STB, TITLE, DATE, PROVIDER, REV, VIEW_TIME)

  sealed trait Field {
    val stringValue: String
    val index: Int
    // -1 if val1 smaller, 0 if equal, 1 if val1 larger
    def compare(val1: AnyRef, val2: AnyRef): Int
  }

  case object STB extends Field {
    override val stringValue: String = "STB"
    override val index = 0
    override def compare(val1: AnyRef, val2: AnyRef): Int = {
      val1.toString.compareTo(val2.toString)
    }
  }

  case object TITLE extends Field {
    override val stringValue: String = "TITLE"
    override val index = 1
    override def compare(val1: AnyRef, val2: AnyRef): Int = {
      val1.toString.compareTo(val2.toString)
    }
  }

  case object DATE extends Field {
    override val stringValue: String = "DATE"
    override val index = 2
    override def compare(val1: AnyRef, val2: AnyRef): Int = {
      val d1 = dayFormat.parse(val1.toString)
      val d2 = dayFormat.parse(val2.toString)
      d1.compareTo(d2)
    }
  }

  case object PROVIDER extends Field {
    override val stringValue: String = "PROVIDER"
    override val index = 3
    override def compare(val1: AnyRef, val2: AnyRef): Int = {
      val1.toString.compareTo(val2.toString)
    }
  }

  case object REV extends Field {
    override val stringValue: String = "REV"
    override val index = 4
    override def compare(val1: AnyRef, val2: AnyRef): Int = {
      val1.toString.toDouble.compareTo(val2.toString.toDouble)
    }
  }

  case object VIEW_TIME extends Field {
    override val stringValue: String = "VIEW_TIME"
    override val index = 5
    override def compare(val1: AnyRef, val2: AnyRef): Int = {
      val d1 = hourFormat.parse(val1.toString)
      val d2 = hourFormat.parse(val2.toString)
      d1.compareTo(d2)
    }
  }
}
