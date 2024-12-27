package DateNight

import DateNight.DateNightPositionConstants.*
import Base.Static
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import Base.CurveRenderer
import com.badlogic.gdx.graphics.Color

object Park extends Static {
  val location = bottomLeft
  var interactLoc = location
  def render(shapeRenderer: ShapeRenderer): Unit = {

    shapeRenderer.setColor(0, 0, 0, 1)

    shapeRenderer.rect(
      rloc()._1,
      rloc()._2,
      StageWidth,
      StageHeight
    )
  }
}

object Road extends Static {
  val location = bottomRight - (4.5f, 0f)
  var interactLoc = location
  def render(shapeRenderer: ShapeRenderer): Unit = {

    shapeRenderer.setColor(0, 0, 0, 1)

    // Main Road
    shapeRenderer.rect(
      rloc()._1,
      rloc()._2,
      2 * boxSize,
      StageHeight
    )

    // Stripes
    val stripeWidth = 0.2f
    val stripeHeight = 0.75f
    for (
      yloc <- BigDecimal(bottomLeft.y + 0.5f) until BigDecimal(
        topLeft.y
      ) by 2 * BigDecimal(stripeHeight)
    ) {
      shapeRenderer.rect(
        rloc()._1 + (1 - stripeWidth / 2) * boxSize,
        rloc()._2 + yloc.toFloat * boxSize,
        stripeWidth * boxSize,
        stripeHeight * boxSize
      )
    }
  }
}

object StatueArea extends Static {
  val location = topLeft + (7, -3)
  var interactLoc = location + (0, 2)
  def render(shapeRenderer: ShapeRenderer): Unit = {

    shapeRenderer.setColor(0, 0, 0, 1)

    shapeRenderer.circle(rloc()._1, rloc()._2, 2 * boxSize)
    shapeRenderer.circle(rloc()._1, rloc()._2, 0.5f * boxSize)

  }
}

object ParkPaths extends Static {
  val location = (-1, -1)
  var interactLoc = location
  def render(shapeRenderer: ShapeRenderer): Unit = {

    shapeRenderer.setColor(0, 0, 0, 1)

    // From statue to road
    shapeRenderer.line(
      StatueArea.rloc()._1 + math.sqrt(15f / 4).toFloat * boxSize,
      StatueArea.rloc()._2 + 0.5f * boxSize,
      Road.rloc()._1 - 0.5f * boxSize,
      StatueArea.rloc()._2 + 0.5f * boxSize
    )
    shapeRenderer.line(
      StatueArea.rloc()._1 + math.sqrt(15f / 4).toFloat * boxSize,
      StatueArea.rloc()._2 - 0.5f * boxSize,
      Road.rloc()._1 - 0.5f * boxSize,
      StatueArea.rloc()._2 - 0.5f * boxSize
    )

    // Boundary of the park by the road
    shapeRenderer.line(
      Road.rloc()._1 - 0.5f * boxSize,
      stageY,
      Road.rloc()._1 - 0.5f * boxSize,
      StatueArea.rloc()._2 - 0.5f * boxSize
    )
    shapeRenderer.line(
      Road.rloc()._1 - 0.5f * boxSize,
      StatueArea.rloc()._2 + 0.5f * boxSize,
      Road.rloc()._1 - 0.5f * boxSize,
      stageY + StageHeight
    )

    // Path from statue to bottom
    CurveRenderer.drawSpline(
      Array(
        Vector2(
          StatueArea.rloc()._1 + 0.5f * boxSize,
          StatueArea.rloc()._2
        ),
        Vector2(
          StatueArea.rloc()._1 + 0.5f * boxSize,
          StatueArea.rloc()._2 - math.sqrt(15f / 4).toFloat * boxSize
        ),
        Vector2(StatueArea.rloc()._1 + 1f * boxSize, stageY + 6 * boxSize),
        Vector2(StatueArea.rloc()._1 + 1f * boxSize, stageY),
        Vector2(StatueArea.rloc()._1 + 1f * boxSize, stageY)
      )
    )
    CurveRenderer.drawSpline(
      Array(
        Vector2(
          StatueArea.rloc()._1 - 0.5f * boxSize,
          StatueArea.rloc()._2
        ),
        Vector2(
          StatueArea.rloc()._1 - 0.5f * boxSize,
          StatueArea.rloc()._2 - math.sqrt(15f / 4).toFloat * boxSize
        ),
        Vector2(StatueArea.rloc()._1, stageY + 6 * boxSize),
        Vector2(StatueArea.rloc()._1, stageY),
        Vector2(StatueArea.rloc()._1, stageY)
      )
    )
  }
}

object River extends Static {
  val location = bottomLeft + (2.5f, 0f)
  var interactLoc = location
  def render(shapeRenderer: ShapeRenderer): Unit = {

    shapeRenderer.setColor(0, 0, 0, 1)

    CurveRenderer.drawSpline(
      Array(
        Vector2(
          River.rloc()._1,
          River.rloc()._2
        ),
        Vector2(
          River.rloc()._1,
          River.rloc()._2
        ),
        Vector2(
          River.rloc()._1 + 0.5f * boxSize,
          River.rloc()._2 + 5 * boxSize
        ),
        Vector2(
          River.rloc()._1,
          River.rloc()._2 + 10 * boxSize
        ),
        Vector2(
          River.rloc()._1 + 0.5f * boxSize,
          River.rloc()._2 + 15 * boxSize
        ),
        Vector2(
          River.rloc()._1 + 0.5f * boxSize,
          River.rloc()._2 + 15 * boxSize
        )
      )
    )

    CurveRenderer.drawSpline(
      Array(
        Vector2(
          River.rloc()._1 - 2.2f * boxSize,
          River.rloc()._2
        ),
        Vector2(
          River.rloc()._1 - 2.2f * boxSize,
          River.rloc()._2
        ),
        Vector2(
          River.rloc()._1 - 1.7f * boxSize,
          River.rloc()._2 + 5 * boxSize
        ),
        Vector2(
          River.rloc()._1 - 2.2f * boxSize,
          River.rloc()._2 + 10 * boxSize
        ),
        Vector2(
          River.rloc()._1 - 1.7f * boxSize,
          River.rloc()._2 + 15 * boxSize
        ),
        Vector2(
          River.rloc()._1 - 1.7f * boxSize,
          River.rloc()._2 + 15 * boxSize
        )
      )
    )
  }

}
