package Base

import com.badlogic.gdx.graphics.g2d.{SpriteBatch, BitmapFont, GlyphLayout}
import com.badlogic.gdx.graphics.{Color, Pixmap, Texture}
import com.badlogic.gdx.math.Vector2

import scala.compiletime.uninitialized
import scala.collection.mutable.Queue
import com.badlogic.gdx.Gdx

class Snippet(speaker: Speech, text: String, outLoud: Boolean = true) {
  def setDialog() = speaker.setDialog(text, outLoud)
  def clearDialog() = speaker.clearDialog()
}

case class ConversationBase(story: Story, dialog: List[Snippet]) {
  def makeConversation() = new Conversation(story, dialog.to(Queue))
}

// TODO, make dialog a tree instead of a queue
class Conversation(story: Story, dialog: Queue[Snippet]) {
  var remainingSnippets = dialog
  var currentSnippet: Snippet = null

  // Returns true if the conversation is over
  def advance(): Boolean = {
    if (currentSnippet != null) currentSnippet.clearDialog()

    if (remainingSnippets.isEmpty) {
      story.dialogFinished = true
      return true
    } else {
      currentSnippet = remainingSnippets.dequeue
      currentSnippet.setDialog()
      return false
    }
  }
}

object DialogController {
  var playerConversation: Option[Conversation] = None
  // var activeConversations= List[Conversation] () // This does not include conversations the player is a part of

  // Returns true if the player doesn't have an active conversation
  // or the conversation is over
  def advancePlayerConversation(): Boolean = {
    playerConversation match
      case Some(c) => c.advance()
      case None    => true
  }

  def setPlayerConversation(convo: ConversationBase): Unit = {
    playerConversation = Some(convo.makeConversation())
    advancePlayerConversation()
  }
}

class SpeechBubble(font: BitmapFont) {
  val padding = 10
  val tailHeight = 20
  val tailWidth = 10
  var background: Texture = uninitialized
  val glyphLayout = new GlyphLayout()
  val backgroundColor = new Color(0.9607843f, 0.9607843f, 0.8627451f, 1)

  def draw(
      batch: SpriteBatch,
      text: String,
      x: Float,
      y: Float,
      width: Float = 1000
  ): Unit =
    glyphLayout.setText(font, text)
    val bubbleWidth = math.min(width, glyphLayout.width + padding * 2)
    val bubbleHeight = glyphLayout.height + padding * 2

    if background == null || background.getWidth != bubbleWidth.toInt ||
      background.getHeight != (bubbleHeight + tailHeight).toInt
    then
      if background != null then background.dispose()
      background = createBubbleTexture(bubbleWidth.toInt, bubbleHeight.toInt)

    batch.draw(
      background,
      x,
      y,
      bubbleWidth + tailWidth,
      bubbleHeight + tailHeight
    )
    font.draw(
      batch,
      text,
      x + tailWidth + padding,
      y + bubbleHeight + tailHeight - padding
    )

  private def createBubbleTexture(width: Int, height: Int): Texture =
    val pixmap =
      new Pixmap(width + tailWidth, height + tailHeight, Pixmap.Format.RGBA8888)

    // Main bubble
    pixmap.setColor(backgroundColor)
    pixmap.fillRectangle(tailWidth, 0, width, height)
    pixmap.setColor(Color.BLACK)
    pixmap.drawRectangle(tailWidth, 0, width, height)

    // Tail
    pixmap.setColor(backgroundColor)
    val points = Array(
      (tailWidth, height / 2),
      (tailWidth + height / 2, height),
      (0, height + tailHeight)
    )
    pixmap.fillTriangle(
      points(0)._1,
      points(0)._2,
      points(1)._1,
      points(1)._2,
      points(2)._1,
      points(2)._2
    )

    pixmap.setColor(Color.BLACK)
    // pixmap.drawLine(
    //   points(0)._1,
    //   points(0)._2,
    //   points(1)._1,
    //   points(1)._2
    // )
    pixmap.drawLine(
      points(0)._1,
      points(0)._2,
      points(2)._1,
      points(2)._2
    )
    pixmap.drawLine(
      points(2)._1,
      points(2)._2,
      points(1)._1,
      points(1)._2
    )

    val texture = new Texture(pixmap)
    pixmap.dispose()
    texture

  def dispose(): Unit =
    if background != null then background.dispose()
}

class ThoughtBubble(font: BitmapFont):
  val padding = 10
  val bubbleSpacing = 8
  // TODO: Make my own thought bubble
  var background: Texture = new Texture(Gdx.files.internal("thoughtbubble.png"))
  val glyphLayout = new GlyphLayout()
  val backgroundColor = new Color(0.9607843f, 0.9607843f, 0.8627451f, 1)

  def draw(
      batch: SpriteBatch,
      text: String,
      x: Float,
      y: Float,
      width: Float = 1000
  ): Unit =
    glyphLayout.setText(font, text)
    val bubbleWidth = math.min(width, (glyphLayout.width + padding) * 2)
    val bubbleHeight =
      math.max((glyphLayout.height + padding) * 2, bubbleWidth * 0.4f)

    // if background == null || background.getWidth != bubbleWidth.toInt ||
    //   background.getHeight != bubbleHeight.toInt + 30
    // then
    //   if background != null then background.dispose()
    //   background = createBubbleTexture(bubbleWidth.toInt, bubbleHeight.toInt)

    batch.draw(background, x, y, bubbleWidth, bubbleHeight + 30)
    font.draw(
      batch,
      text,
      x + bubbleWidth / 2 - glyphLayout.width / 2,
      y + bubbleHeight / 2 + glyphLayout.height * 2
    )

  private def createBubbleTexture(width: Int, height: Int): Texture =
    val pixmap = new Pixmap(width, height + 30, Pixmap.Format.RGBA8888)
    pixmap.setColor(backgroundColor)

    // Main bubble (oval)
    for i <- 0 until 360 by 10 do
      val rad = math.toRadians(i)
      val px = width / 2 + (width / 2 * math.cos(rad)).toInt
      val py = height / 2 + 30 + (height / 2 * math.sin(rad)).toInt
      pixmap.fillCircle(px, py, 10)

    // Small thought bubbles
    val bubbleSizes = Array(8, 6, 4)
    for (i, size) <- bubbleSizes.zipWithIndex do
      pixmap.fillCircle(width / 2, 15 - i * bubbleSpacing, size)

    val texture = new Texture(pixmap)
    pixmap.dispose()
    texture

  def dispose(): Unit =
    if background != null then background.dispose()
