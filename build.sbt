// *****************************************************************************
// Projects
// *****************************************************************************

// Calculate the current year for usage in copyright notices and license headers.
lazy val currentYear: Int = java.time.OffsetDateTime.now().getYear

lazy val dfasdlUtils =
  project
    .in(file("."))
    .enablePlugins(
      AutomateHeaderPlugin,
      GitBranchPrompt,
      GitVersioning,
      GhpagesPlugin,
      SiteScaladocPlugin
    )
    .settings(settings)
    .settings(
      name := "dfasdl-utils",
      libraryDependencies ++= Seq(
        library.cats,
        library.dfasdlCore,
        library.shapeless,
        library.scalaCheck     % Test,
        library.scalaCheckTbDT % Test,
        library.scalaTest      % Test
      )
    )

lazy val benchmarks =
  project
    .in(file("benchmarks"))
    .enablePlugins(AutomateHeaderPlugin, JmhPlugin)
    .settings(settings)
    .settings(
      name := "dfasdl-utils-benchmarks",
      libraryDependencies ++= Seq(
        library.scalaCheck % Test,
        library.scalaTest  % Test
      ),
      publishArtifact := false
    )
    .dependsOn(dfasdlUtils)

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val cats         = "0.9.0"
      val dfasdlCore   = "1.0"
      val scalaCheck   = "1.13.5"
      val scalaCheckTb = "0.2.2"
      val scalaTest    = "3.0.4"
      val shapeless    = "2.3.2"
    }
    val cats           = "org.typelevel"  %% "cats"                        % Version.cats
    val dfasdlCore     = "org.dfasdl"     %% "dfasdl-core"                 % Version.dfasdlCore
    val scalaCheck     = "org.scalacheck" %% "scalacheck"                  % Version.scalaCheck
    val scalaCheckTbDT = "com.47deg"      %% "scalacheck-toolbox-datetime" % Version.scalaCheckTb
    val scalaTest      = "org.scalatest"  %% "scalatest"                   % Version.scalaTest
    val shapeless      = "com.chuusai"    %% "shapeless"                   % Version.shapeless
  }

// *****************************************************************************
// Settings
// *****************************************************************************

lazy val settings =
commonSettings ++
documentationSettings ++
gitSettings ++
publishSettings ++
resolverSettings ++
scalafmtSettings

lazy val commonSettings =
  Seq(
    scalaVersion in ThisBuild := "2.12.3",
    crossScalaVersions := Seq("2.12.3", "2.11.11"),
    organization := "org.dfasdl",
    organizationName := "Wegtam GmbH",
    startYear := Option(2014),
    licenses += ("AGPL-V3", url("https://www.gnu.org/licenses/agpl.html")),
    headerLicense := Some(
      HeaderLicense.AGPLv3(s"2014 - $currentYear", "Contributors as noted in the AUTHORS.md file")
    ),
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding",
      "UTF-8",
      "-feature",
      "-language:_",
      "-target:jvm-1.8",
      "-unchecked",
      "-Xfatal-warnings",
      "-Xfuture",
      "-Xlint",
      "-Ydelambdafy:method",
      "-Yno-adapted-args",
      "-Ywarn-numeric-widen",
      "-Ywarn-unused-import",
      "-Ywarn-value-discard"
    ),
    scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 11)) => Seq("-Xmax-classfile-name", "78") // Workaround for SI-3623.
        case _             => Seq()
      }
    },
    incOptions := incOptions.value.withNameHashing(nameHashing = true),
    unmanagedSourceDirectories.in(Compile) := Seq(scalaSource.in(Compile).value),
    unmanagedSourceDirectories.in(Test) := Seq(scalaSource.in(Test).value),
    wartremoverWarnings in (Compile, compile) ++= Warts.unsafe
  )

lazy val documentationSettings =
  Seq(
    autoAPIMappings := true,
    ghpagesNoJekyll := true,
    git.remoteRepo := "git@github.com:DFASDL/dfasdl-utils.git"
  )

lazy val gitSettings =
  Seq(
    git.useGitDescribe := true
  )

lazy val publishSettings =
  Seq(
    bintrayOrganization := Option("wegtam"),
    bintrayPackage := "dfasdl-utils",
    bintrayReleaseOnPublish in ThisBuild := false,
    bintrayRepository := "dfasdl",
    developers += Developer(
      "wegtam",
      "Wegtam GmbH",
      "tech@wegtam.com",
      url("https://www.wegtam.com")
    ),
    homepage := Option(url("https://github.com/DFASDL/dfasdl-utils")),
    pomIncludeRepository := (_ => false),
    publishArtifact in Test := false,
    publish := (publish dependsOn (test in Test)).value,
    scmInfo := Option(
      ScmInfo(
        url("https://github.com/DFASDL/dfasdl-utils"),
        "git@github.com:DFASDL/dfasdl-utils.git"
      )
    )
  )

lazy val resolverSettings =
  Seq(
    resolvers += "DFASDL" at "https://dl.bintray.com/wegtam/dfasdl"
  )

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true,
    scalafmtOnCompile.in(Sbt) := false,
    scalafmtVersion := "1.2.0"
  )

