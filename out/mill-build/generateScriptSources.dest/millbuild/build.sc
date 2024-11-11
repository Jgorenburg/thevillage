package millbuild

import _root_.mill.runner.MillBuildRootModule

object MiscInfo_build {
  implicit lazy val millBuildRootModuleInfo: _root_.mill.runner.MillBuildRootModule.Info = _root_.mill.runner.MillBuildRootModule.Info(
    Vector("C:\\Program Files\\Java\\jre1.8.0_421\\lib\\ext\\access-bridge-64.jar", "C:\\Program Files\\Java\\jre1.8.0_421\\lib\\ext\\cldrdata.jar", "C:\\Program Files\\Java\\jre1.8.0_421\\lib\\ext\\dnsns.jar", "C:\\Program Files\\Java\\jre1.8.0_421\\lib\\ext\\jaccess.jar", "C:\\Program Files\\Java\\jre1.8.0_421\\lib\\ext\\jfxrt.jar", "C:\\Program Files\\Java\\jre1.8.0_421\\lib\\ext\\localedata.jar", "C:\\Program Files\\Java\\jre1.8.0_421\\lib\\ext\\nashorn.jar", "C:\\Program Files\\Java\\jre1.8.0_421\\lib\\ext\\sunec.jar", "C:\\Program Files\\Java\\jre1.8.0_421\\lib\\ext\\sunjce_provider.jar", "C:\\Program Files\\Java\\jre1.8.0_421\\lib\\ext\\sunmscapi.jar", "C:\\Program Files\\Java\\jre1.8.0_421\\lib\\ext\\sunpkcs11.jar", "C:\\Program Files\\Java\\jre1.8.0_421\\lib\\ext\\zipfs.jar", "C:\\Program Files\\Java\\jre1.8.0_421\\lib\\resources.jar", "C:\\Program Files\\Java\\jre1.8.0_421\\lib\\rt.jar", "C:\\Program Files\\Java\\jre1.8.0_421\\lib\\jsse.jar", "C:\\Program Files\\Java\\jre1.8.0_421\\lib\\jce.jar", "C:\\Program Files\\Java\\jre1.8.0_421\\lib\\charsets.jar", "C:\\Program Files\\Java\\jre1.8.0_421\\lib\\jfr.jar", "C:\\Users\\jacob\\Desktop\\Coding Work\\thevillage\\out\\mill-launcher\\0.11.4.jar").map(_root_.os.Path(_)),
    _root_.os.Path("C:\\Users\\jacob\\Desktop\\Coding Work\\thevillage"),
    _root_.os.Path("C:\\Users\\jacob\\Desktop\\Coding Work\\thevillage"),
    _root_.scala.Seq()
  )
  implicit lazy val millBaseModuleInfo: _root_.mill.main.RootModule.Info = _root_.mill.main.RootModule.Info(
    millBuildRootModuleInfo.projectRoot,
    _root_.mill.define.Discover[build]
  )
}
import MiscInfo_build.{millBuildRootModuleInfo, millBaseModuleInfo}
object build extends build
class build extends _root_.mill.main.RootModule {

//MILL_ORIGINAL_FILE_PATH=C:\Users\jacob\Desktop\Coding Work\thevillage\build.sc
//MILL_USER_CODE_START_MARKER
/** This is the Mill build script for this LibGDX project.
  *
  * For a full list of available fields / overrides for the modules, please take
  * a look at the source code for `JavaModule` and `ScalaModule` from Mill:
  * `JavaModule`:
  * https://github.com/com-lihaoyi/mill/blob/main/scalalib/src/JavaModule.scala
  * `ScalaModule`:
  * https://github.com/com-lihaoyi/mill/blob/main/scalalib/src/ScalaModule.scala
  */

/** Quick note on the Ivy dependencies listed below: Scala dependencies use
  * `ivy"org::name:version"`; Java dependencies use `ivy"org:name:version"`.
  * Note that Scala dependencies have *two* colons between `org` and `name`.
  *
  * Additionally, please note that if you need to select a specific JAR in a
  * dependency, append `;classifier=jar-name` to the end.
  *
  * e.g., `api "com.badlogicgames.gdx:gdx-platform:1.11.0:natives-desktop"` in
  * Gradle becomes
  * `ivy"com.badlogicgames.gdx:gdx-platform:1.11.0;classifier=natives-desktop"`
  *
  * Please read this for more info about Ivy dependencies:
  * https://com-lihaoyi.github.io/mill/mill/Configuring_Mill.html#_adding_ivy_dependencies
  */

import mill._
import scala.sys
// Scala-specific imports for Mill, e.g., `ScalaModule`
import scalalib._
import scalalib.scalafmt._

/** Simple record object to hold version numbers
  */
object versions {
  val scala = "3.5.1"
  val gdx = "1.11.0"
  val munit = "1.0.0"
}

/** Simple record object to hold organization identifiers
  */
object orgs {
  val gdx = "com.badlogicgames.gdx"
}

object ScalacOptions {
  lazy val compile: Seq[String] =
    Seq(
      "-deprecation", // Emit warning and location for usages of deprecated APIs.
      "-encoding",
      "utf-8", // Specify character encoding used by source files.
      "-feature", // Emit warning and location for usages of features that should be imported explicitly.
      "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
      "-language:experimental.macros", // Allow macro definition (besides implementation and application)
      "-language:higherKinds", // Allow higher-kinded types
      "-language:implicitConversions", // Allow definition of implicit functions called views
      "-language:unsafeNulls",
      "-unchecked", // Enable additional warnings where generated code depends on assumptions.
      "-Xfatal-warnings", // Fail the compilation if there are any warnings.
      "-Yexplicit-nulls",
      "-Wsafe-init"
    )

  lazy val test: Seq[String] =
    Seq(
      "-deprecation", // Emit warning and location for usages of deprecated APIs.
      "-encoding",
      "utf-8", // Specify character encoding used by source files.
      "-feature", // Emit warning and location for usages of features that should be imported explicitly.
      "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
      "-language:experimental.macros", // Allow macro definition (besides implementation and application)
      "-language:higherKinds", // Allow higher-kinded types
      "-language:implicitConversions", // Allow definition of implicit functions called views
      "-unchecked", // Enable additional warnings where generated code depends on assumptions.
      "-Yexplicit-nulls",
      "-Wsafe-init"
    )
}

/** This is the `core` project. The name of this module corresponds to the
  * folder name.
  */
// object core extends ScalaModule with ScalafmtModule {
//   override def scalaVersion = versions.scala

//   override def ivyDeps = Agg(
//     ivy"${orgs.gdx}:gdx:${versions.gdx}"
//   )

//   override def scalacOptions = ScalacOptions.compile

//   object test extends ScalaTests with TestModule.Munit with ScalafmtModule {
//     override def ivyDeps = Agg(
//       ivy"org.scalameta::munit::${versions.munit}"
//     )

//     override def scalacOptions = ScalacOptions.test
//   }
// }

// /** This is the `lib` project. The name of this module corresponds to the folder name.
//   */
// object lib extends ScalaModule with ScalafmtModule {
//   override def scalaVersion = versions.scala

//   override def scalacOptions = ScalacOptions.compile

//   object test extends ScalaTests with TestModule.Munit with ScalafmtModule {
//     override def ivyDeps = Agg(
//       ivy"org.scalameta::munit::${versions.munit}"
//     )

//     override def scalacOptions = ScalacOptions.test
//   }
// }

// /** This is the `desktop` project. This project uses the `core` project as a library, adds in the
//   * assets, and runs the game on your desktop.
//   */
// object desktop extends ScalaModule with ScalafmtModule {
//   override def scalaVersion = versions.scala

/** Modules that this module depends on. This module depends on `core` defined
  * above since it holds the logic for the game. //
  */
// override def moduleDeps = Seq(core)

// override def ivyDeps = Agg(
//   ivy"${orgs.gdx}:gdx-backend-lwjgl3:${versions.gdx}",
//   ivy"${orgs.gdx}:gdx-platform:${versions.gdx};classifier=natives-desktop"
// )

// override def scalacOptions = ScalacOptions.compile

// // /** Resources for this module. Like the `sourceSets.main.resources.srcDirs = ["../assets"]` line
// //   * in the default Java & Gradle LibGDX template, this sets the resources to be the `assets`
// //   * folder in the project root.
// //   */
// override def resources = T.sources { T.workspace / "assets" }

// // /** Mill passes these arguments to the JVM. Please don't confuse these with the `.mill-jvm-opts`
// //   * mentioned in the Mill docs - those are for Mill itself. These are for the actual program you
// //   * are running.
// //   *
// //   * On macOS, LWGL requires that it be started on the first thread. The program will throw an
// //   * exception otherwise.
// //   */
// override def forkArgs = T {
//   if (sys.props("os.name") == "Mac OS X")
//     Seq("-XstartOnFirstThread")
//   else
//     Seq.empty
// }
// }

object game extends ScalaModule with ScalafmtModule {
  override def scalaVersion = versions.scala

  override def ivyDeps = Agg(
    ivy"${orgs.gdx}:gdx:${versions.gdx}",
    ivy"${orgs.gdx}:gdx-backend-lwjgl3:${versions.gdx}",
    ivy"${orgs.gdx}:gdx-platform:${versions.gdx};classifier=natives-desktop"
  )

  override def scalacOptions = ScalacOptions.compile

  // /** Resources for this module. Like the `sourceSets.main.resources.srcDirs = ["../assets"]` line
  //   * in the default Java & Gradle LibGDX template, this sets the resources to be the `assets`
  //   * folder in the project root.
  //   */
  override def resources = T.sources { T.workspace / "assets" }

  // /** Mill passes these arguments to the JVM. Please don't confuse these with the `.mill-jvm-opts`
  //   * mentioned in the Mill docs - those are for Mill itself. These are for the actual program you
  //   * are running.
  //   *
  //   * On macOS, LWGL requires that it be started on the first thread. The program will throw an
  //   * exception otherwise.
  //   */
  override def forkArgs = T {
    if (sys.props("os.name") == "Mac OS X")
      Seq("-XstartOnFirstThread")
    else
      Seq.empty
  }

//   object test extends ScalaTests with TestModule.Munit with ScalafmtModule {
//     override def ivyDeps = Agg(
//       ivy"org.scalameta::munit::${versions.munit}"
//     )

//     override def scalacOptions = ScalacOptions.test
}

}