package Base

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.Vector2
import scala.compiletime.uninitialized

import Base.BoxCoords.boxSize
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.graphics.Color

object CurveRenderer {

  var shapeRenderer: ShapeRenderer = uninitialized

  def setRenderer(sr: ShapeRenderer) = shapeRenderer = sr

  // Calculates a point on a Catmull-Rom spline at t position
  private def calculateSplinePoint(
      p0: Vector2,
      p1: Vector2,
      p2: Vector2,
      p3: Vector2,
      t: Float,
      alpha: Float = 0.5f
  ): Vector2 = {
    val t2 = t * t
    val t3 = t2 * t

    // Catmull-Rom matrix coefficients
    val a = -alpha * t3 + 2f * alpha * t2 - alpha * t
    val b = (2f - alpha) * t3 + (alpha - 3f) * t2 + 1f
    val c = (alpha - 2f) * t3 + (3f - 2f * alpha) * t2 + alpha * t
    val d = alpha * t3 - alpha * t2

    new Vector2(
      a * p0.x + b * p1.x + c * p2.x + d * p3.x,
      a * p0.y + b * p1.y + c * p2.y + d * p3.y
    )
  }

  // Draws a smooth spline through the given control points
  def drawSpline(controlPoints: Array[Vector2], segments: Int = 20): Unit = {
    if (controlPoints.length < 4) return

    // For each set of 4 control points
    for (i <- 0 until controlPoints.length - 3) {
      val p0 = controlPoints(i)
      val p1 = controlPoints(i + 1)
      val p2 = controlPoints(i + 2)
      val p3 = controlPoints(i + 3)

      // Draw segments between calculated points
      var prevPoint = calculateSplinePoint(p0, p1, p2, p3, 0f)

      for (j <- 1 to segments) {
        val t = j.toFloat / segments
        val currentPoint = calculateSplinePoint(p0, p1, p2, p3, t)

        shapeRenderer.line(prevPoint, currentPoint)
        prevPoint = currentPoint
      }
    }
  }

  def drawArcSegment(
      x: Float,
      y: Float, // Center coordinates
      radius: Float,
      startAngle: Float, // In degrees
      sweepAngle: Float
  ): Unit = { // In degrees

    // Calculate start, end, and control points
    val startRad = math.toRadians(startAngle)
    val endRad = math.toRadians(startAngle + sweepAngle)
    val midRad = startRad + (endRad - startRad) / 2

    // Start point
    val x1 = x + radius * math.cos(startRad).toFloat
    val y1 = y + radius * math.sin(startRad).toFloat

    // Control points
    val cx1 = x + radius * 1.3f * math.cos(startRad).toFloat
    val cy1 = y + radius * 1.3f * math.sin(startRad).toFloat
    val cx2 = x + radius * 1.3f * math.cos(endRad).toFloat
    val cy2 = y + radius * 1.3f * math.sin(endRad).toFloat

    // End point
    val x2 = x + radius * math.cos(endRad).toFloat
    val y2 = y + radius * math.sin(endRad).toFloat

    shapeRenderer.curve(
      x1,
      y1, // Start point
      cx1,
      cy1, // First control point
      cx2,
      cy2, // Second control point
      x2,
      y2, // End point
      20 // Segments
    )
  }

}
