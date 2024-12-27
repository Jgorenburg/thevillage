import com.badlogic.gdx.graphics.g2d.{Animation, TextureRegion}

// Later in code:
val anim: Animation[TextureRegion] = ???
val frame: TextureRegion = anim.getKeyFrame(0f, true)
