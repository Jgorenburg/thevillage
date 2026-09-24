package AssetTools

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import Base.PlaceholderSpec
import Snowedin.SnowedInSprites

/** Writes one solid-colour PNG (with a 1px darker border) per region in
  * `SnowedInSprites.placeholders` into the given directory. Character frames
  * get a few marker pixels so facing and walk frames are distinguishable.
  *
  * Usage: `./millw game.genPlaceholders`
  */
object GenPlaceholders {
  def main(args: Array[String]): Unit = {
    val outDir = new File(args.headOption.getOrElse("art/placeholders"))
    outDir.mkdirs()
    // Remove stale placeholders so renamed regions do not linger
    Option(outDir.listFiles()).toList.flatten
      .filter(_.getName.endsWith(".png"))
      .foreach(_.delete())

    val specs = SnowedInSprites.placeholders
    specs.foreach(spec => write(spec, new File(outDir, s"${spec.name}.png")))
    println(s"Wrote ${specs.length} placeholders to ${outDir.getPath}")
  }

  private def argb(r: Float, g: Float, b: Float): Int = {
    def c(v: Float) = math.max(0, math.min(255, math.round(v * 255)))
    (0xff << 24) | (c(r) << 16) | (c(g) << 8) | c(b)
  }

  def write(spec: PlaceholderSpec, file: File): Unit = {
    val (w, h) = (spec.width, spec.height)
    val col = spec.color
    val fill = argb(col.r, col.g, col.b)
    val border = argb(col.r * 0.6f, col.g * 0.6f, col.b * 0.6f)
    val mark = argb(
      col.r * 0.35f + 0.65f,
      col.g * 0.35f + 0.65f,
      col.b * 0.35f + 0.65f
    )

    val img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
    for {
      x <- 0 until w
      y <- 0 until h
    } {
      val edge = x == 0 || y == 0 || x == w - 1 || y == h - 1
      img.setRGB(x, y, if (edge) border else fill)
    }

    // Character markers (image y grows downwards)
    def dot(x: Int, y: Int): Unit =
      if (x > 0 && y > 0 && x < w - 1 && y < h - 1) img.setRGB(x, y, mark)
    val n = spec.name
    if (n.contains("_down")) { dot(5, 5); dot(10, 5) } // two eyes
    if (n.contains("_side")) dot(11, 5) // one eye, facing right
    if (n.contains("_walk_")) {
      // a foot marker that steps across the four frames
      val frame = n.takeRight(1).toIntOption.getOrElse(0)
      val fx = 3 + frame * 3
      dot(fx, h - 3); dot(fx + 1, h - 3)
    }
    if (n.endsWith("_sit")) (2 until w - 2).foreach(x => dot(x, h - 8))
    // Object animation frames (e.g. fireplace_lit_1): a stripe that moves
    // with the frame index, so frames differ and are not packed as aliases
    if (!n.contains("_walk_") && n.matches(".*_\\d+"))
      val frame = n.split('_').last.toInt
      (1 until w - 1).foreach(x => dot(x, h / 3 + frame * 2))

    ImageIO.write(img, "png", file)
  }
}
