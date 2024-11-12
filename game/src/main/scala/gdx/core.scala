package core

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.ScreenUtils

class MyGdxGame extends ApplicationAdapter:
  var batch: SpriteBatch | Null = null
  var img: Texture | Null = null

  var tick = 0.0f

  override def create(): Unit =
    batch = SpriteBatch()
    img = Texture("badlogic.jpg")

  override def resize(width: Int, height: Int): Unit = ()

  override def render(): Unit =
    ScreenUtils.clear(1, 0, 0, 1)
    tick += 1
    batch.begin()
    batch.draw(img, tick, tick)
    batch.end()

  override def pause(): Unit = ()

  override def resume(): Unit = ()

  override def dispose(): Unit =
    batch.dispose()
    img.dispose()
