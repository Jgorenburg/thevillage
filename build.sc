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

  /** Game code lives in `src/main/scala`; tests in `src/test/scala`. */
  override def sources = T.sources { millSourcePath / "src" / "main" / "scala" }

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

  /** Writes solid-colour placeholder PNGs for every sprite region into
    * `art/placeholders/`. Run with `./millw game.genPlaceholders`.
    */
  def genPlaceholders() = T.command {
    mill.util.Jvm.runSubprocess(
      mainClass = "AssetTools.GenPlaceholders",
      classPath = assettools.runClasspath().map(_.path),
      jvmArgs = Seq("-Djava.awt.headless=true"),
      mainArgs = Seq((T.workspace / "art" / "placeholders").toString),
      workingDir = T.workspace
    )
  }

  /** Packs `art/placeholders/` (with `art/export/` overriding same-named
    * files) into `assets/atlas/game.atlas`. Run with `./millw game.packAssets`.
    */
  def packAssets() = T.command {
    mill.util.Jvm.runSubprocess(
      mainClass = "AssetTools.PackAssets",
      classPath = assettools.runClasspath().map(_.path),
      jvmArgs = Seq("-Djava.awt.headless=true"),
      mainArgs = Seq(
        (T.workspace / "art" / "placeholders").toString,
        (T.workspace / "art" / "export").toString,
        (T.workspace / "assets" / "atlas").toString,
        "game"
      ),
      workingDir = T.workspace
    )
  }

  object test extends ScalaTests with TestModule.Munit with ScalafmtModule {
    override def ivyDeps = Agg(
      ivy"org.scalameta::munit::${versions.munit}"
    )

    override def sources = T.sources {
      millSourcePath / os.up / "src" / "test" / "scala"
    }

    override def scalacOptions = ScalacOptions.test
  }
}

/** Build-time asset tooling (placeholder generator + TexturePacker). Kept in
  * its own module so gdx-tools never lands on the game's runtime classpath.
  */
object assettools extends ScalaModule with ScalafmtModule {
  override def scalaVersion = versions.scala

  override def moduleDeps = Seq(game)

  override def ivyDeps = Agg(
    ivy"${orgs.gdx}:gdx-tools:${versions.gdx}"
  )

  override def scalacOptions = ScalacOptions.compile
}
