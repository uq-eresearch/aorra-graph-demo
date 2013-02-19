package au.edu.uq.aorra.charts

object ChartData {

  type GrazingPracticeDataKey = (Group.Value, Rating.Value)
  type LandPracticeDataKey = (Reading.Value, Group.Value, Rating.Value)

  object Rating extends Enumeration {
    val A, B, C, D = Value
  }

  object Group extends Enumeration {
    val Previous, Current = Value
  }

  object Reading extends Enumeration {
    val Nutrients, Herbicides, Soil = Value
  }

}

