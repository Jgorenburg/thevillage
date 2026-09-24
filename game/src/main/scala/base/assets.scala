package Base

import scala.collection.mutable.{HashMap, HashSet}
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.{Color, Pixmap, Texture}
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.{Animation, TextureAtlas, TextureRegion}
import com.badlogic.gdx.utils.{Array as GdxArray}

/** Loads the sprite atlas and hands out regions / animations by name.
  *
  * Region names follow TexturePacker's convention: a file called
  * `father_walk_down_2.png` becomes region `father_walk_down` with index 2, so
  * `animation("father_walk_down", ...)` returns all of its frames in order.
  *
  * A missing region never crashes the game: it is drawn as a magenta 16x16
  * placeholder and logged once.
  */
object Assets {
  val AtlasPath = "atlas/game.atlas"
  private val LogTag = "Assets"

  private var atlas: Option[TextureAtlas] = None
  private var missingTexture: Option[Texture] = None
  private var missingRegion: Option[TextureRegion] = None
  private val warned: HashSet[String] = HashSet()
  private val animations: HashMap[(String, Float), Animation[TextureRegion]] =
    HashMap()

  /** Must be called from `create()`, once the GL context exists. */
  def load(): Unit = {
    val pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888)
    pixmap.setColor(Color.MAGENTA)
    pixmap.fill()
    val tex = new Texture(pixmap)
    pixmap.dispose()
    missingTexture = Some(tex)
    missingRegion = Some(new TextureRegion(tex))

    // Internal files fall back to the classpath, which holds `assets/`
    val file = Gdx.files.internal(AtlasPath)
    if (!file.exists()) {
      Gdx.app.error(
        LogTag,
        s"Atlas '$AtlasPath' not found; run `./millw game.packAssets`. " +
          "Every sprite will be drawn as a magenta placeholder."
      )
    } else {
      val loaded = new TextureAtlas(file)
      loaded.getTextures.forEach(
        _.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)
      )
      atlas = Some(loaded)
    }
  }

  private def missing(name: String): TextureRegion = {
    if (warned.add(name)) {
      val msg = s"Missing atlas region '$name', drawing placeholder"
      if (Gdx.app != null) Gdx.app.error(LogTag, msg) else println(msg)
    }
    missingRegion.getOrElse(
      throw new IllegalStateException("Assets.load() was not called")
    )
  }

  def has(name: String): Boolean =
    atlas.exists(_.findRegion(name) != null)

  /** A single region (the first frame if the name has indexed frames). */
  def region(name: String): TextureRegion =
    atlas.flatMap(a => Option(a.findRegion(name))).getOrElse(missing(name))

  /** All `name_N` frames as a looping animation. A name without indexed frames
    * yields a one-frame animation; an unknown name yields the magenta
    * placeholder.
    */
  def animation(
      name: String,
      frameDuration: Float
  ): Animation[TextureRegion] =
    animations.getOrElseUpdate(
      (name, frameDuration), {
        val frames = new GdxArray[TextureRegion]()
        atlas.foreach(_.findRegions(name).forEach(r => frames.add(r)))
        if (frames.isEmpty) frames.add(missing(name))
        new Animation[TextureRegion](
          frameDuration,
          frames,
          Animation.PlayMode.LOOP
        )
      }
    )

  def dispose(): Unit = {
    atlas.foreach(_.dispose())
    missingTexture.foreach(_.dispose())
    atlas = None
    missingTexture = None
    missingRegion = None
    animations.clear()
    warned.clear()
  }
}
