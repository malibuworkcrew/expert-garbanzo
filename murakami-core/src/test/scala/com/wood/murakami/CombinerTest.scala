package com.wood.murakami

import com.wood.murakami.directory.Fields
import com.wood.murakami.query._
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class CombinerTest extends Specification {
  import Fields._

  "Combiner" should {
    "group lines" in {
      //val allFieldsSelector = Seq(Selector(STB, None), Selector(TITLE, )
      //val combiner = Combiner(Seq(Selector()))
      true mustEqual true
    }
  }
}