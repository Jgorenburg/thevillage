package AssetTools

import java.io.File
import java.nio.file.{Files, StandardCopyOption}
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.tools.texturepacker.TexturePacker

/** Packs every PNG from the input folders into one atlas. Later folders win
  * when two files share a name, so real art in `art/export/` replaces the
  * matching placeholder from `art/placeholders/`.
  *
  * Usage: `./millw game.packAssets` which runs `PackAssets <placeholders>
  * <export> <outDir> <atlasName>`
  */
object PackAssets {
  def main(args: Array[String]): Unit = {
    require(
      args.length >= 3,
      "usage: PackAssets <inputDir>... <outDir> <atlasName>"
    )
    val atlasName = args.last
    val outDir = new File(args(args.length - 2))
    val inputs = args.dropRight(2).map(new File(_)).filter(_.isDirectory)

    val staging = Files.createTempDirectory("snowedin-pack").toFile
    var count = 0
    inputs.foreach(dir =>
      Option(dir.listFiles()).toList.flatten
        .filter(f => f.isFile && f.getName.endsWith(".png"))
        .foreach { f =>
          Files.copy(
            f.toPath,
            new File(staging, f.getName).toPath,
            StandardCopyOption.REPLACE_EXISTING
          )
          count += 1
        }
    )

    outDir.mkdirs()
    Option(outDir.listFiles()).toList.flatten
      .filter(f =>
        f.getName == s"$atlasName.atlas" ||
          (f.getName.startsWith(atlasName) && f.getName.endsWith(".png"))
      )
      .foreach(_.delete())

    val settings = new TexturePacker.Settings()
    settings.filterMin = TextureFilter.Nearest
    settings.filterMag = TextureFilter.Nearest
    settings.duplicatePadding = true
    settings.paddingX = 2
    settings.paddingY = 2
    settings.maxWidth = 2048
    settings.maxHeight = 2048
    settings.stripWhitespaceX = false
    settings.stripWhitespaceY = false
    settings.useIndexes = true // `name_N.png` -> region `name`, index N

    TexturePacker.process(
      settings,
      staging.getPath,
      outDir.getPath,
      atlasName
    )

    Option(staging.listFiles()).toList.flatten.foreach(_.delete())
    staging.delete()
    println(
      s"Packed $count images from ${inputs.map(_.getPath).mkString(", ")} " +
        s"into ${new File(outDir, atlasName + ".atlas").getPath}"
    )
  }
}
