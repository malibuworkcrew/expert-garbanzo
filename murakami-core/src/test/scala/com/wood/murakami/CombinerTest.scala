package com.wood.murakami

import com.wood.murakami.directory.Fields
import com.wood.murakami.query._
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class CombinerTest extends Specification {
  import Fields._

  def basicCombiner(selector: Seq[Selector], filter: Option[Filter]): SelectionCombiner = {
    val combiner = SelectionCombiner(selector, filter, None)
    addToCombiner(combiner)
    combiner
  }

  def groupCombiner(selector: Seq[Selector], filter: Option[Filter], group: Field): AggregateCombiner = {
    val combiner = AggregateCombiner(selector, filter, group, None)
    addToCombiner(combiner)
    combiner
  }

  def addToCombiner(combo: Combiner): Unit = {
    combo += "stb1|the matrix|2014-04-01|warner bros|4.00|1:30"
    combo += "stb2|the hobbit|2014-04-02|warner bros|8.00|2:45"
    combo += "stb2|the matrix|2014-04-02|netflix|4.50|11:30"
  }

  "Combiner" should {
    "select lines" in {
      val fieldsSelector = Seq(Selector(STB, None), Selector(TITLE, None))
      val combiner = basicCombiner(fieldsSelector, None)
      combiner.outputString mustEqual
        "stb1,the matrix\nstb2,the hobbit\nstb2,the matrix"
    }

    "filter lines" in {
      val fieldsSelector = Seq(Selector(STB, None), Selector(TITLE, None))
      val filter = Some(Filter(Equality(Fields.TITLE, "the matrix")))
      val combiner = basicCombiner(fieldsSelector, filter)
      combiner.outputString mustEqual
        "stb1,the matrix\nstb2,the matrix"
    }

    "group lines" in {
      val fieldsSelector = Seq(Selector(STB, Some(Collect())), Selector(TITLE, None),
        Selector(REV, Some(Sum())), Selector(PROVIDER, Some(Count())))
      val combiner = groupCombiner(fieldsSelector, None, TITLE)
      combiner.outputString mustEqual
        "[stb1,stb2],the matrix,8.5,2\n[stb2],the hobbit,8.0,1"
    }
  }
}