package DateNight

import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.InputAdapter

import Base.Direction.*
import Base.DialogController

object DateNightInputProcessor extends InputAdapter {
  override def keyDown(keycode: Int): Boolean = {
    keycode match
      case Keys.A => Player.startMoving(Left)
      case Keys.D => Player.startMoving(Right)
      case Keys.W => Player.startMoving(Up)
      case Keys.S => Player.startMoving(Down)
      case Keys.ENTER =>
        if (DialogController.advancePlayerConversation()) Player.ping()
      case _ =>
    return true;
  }

  override def keyUp(keycode: Int): Boolean = {
    keycode match
      case Keys.A => Player.stopMoving(Left)
      case Keys.D => Player.stopMoving(Right)
      case Keys.W => Player.stopMoving(Up)
      case Keys.S => Player.stopMoving(Down)
      case _      =>
    return true;
  }

  override def keyTyped(character: Char): Boolean = {
    return false;
  }

}
