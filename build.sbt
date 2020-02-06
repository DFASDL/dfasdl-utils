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
        library.dfasdlCore,
	library.jaxbApi,
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
        library.jai,
        library.jamm,
        library.scalaCheck % Test,
        library.scalaTest  % Test
      ),
      publishArtifact := false,
      javaOptions in run ++= (dependencyClasspath in Compile).map(makeAgentOptions).value,
      javaOptions in run ++= Seq(
        "-Xms2g",
        "-Xmx2g",
        "-XX:MaxMetaspaceSize=1g"
      ),
      fork in run := true
    )
    .dependsOn(dfasdlUtils)

/**
  * Helper function to generate options for instrumenting memory analysis.
  *
  * @param cp The current classpath.
  * @return A list of options (strings).
  */
def makeAgentOptions(cp: Classpath): Seq[String] = {
  val jammJar = cp.map(_.data).filter(_.toString.contains("jamm")).headOption.map(j => s"-javaagent:$j")
  val jaiJar = cp.map(_.data).filter(_.toString.contains("instrumenter")).headOption.map(j => s"-javaagent:$j")
  Seq(jammJar, jaiJar).flatten
}

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val dfasdlCore   = "1.0.1"
      val jai          = "3.0.1"
      val jamm         = "0.3.2"
      val jaxbApi      = "2.3.2" // Disabled in JDK 9+, Removed in 11+
      val scalaCheck   = "1.14.3"
      val scalaCheckTb = "0.3.1"
      val scalaTest    = "3.0.8"
    }
    val dfasdlCore     = "org.dfasdl"         %% "dfasdl-core"                 % Version.dfasdlCore
    val jaxbApi        = "jakarta.xml.bind"   %  "jakarta.xml.bind-api"        % Version.jaxbApi
    val scalaCheck     = "org.scalacheck"     %% "scalacheck"                  % Version.scalaCheck
    val scalaCheckTbDT = "com.47deg"          %% "scalacheck-toolbox-datetime" % Version.scalaCheckTb
    val scalaTest      = "org.scalatest"      %% "scalatest"                   % Version.scalaTest
    // Dependencies for instrumenting and profiling.
    val jai = "com.google.code.java-allocation-instrumenter" % "java-allocation-instrumenter" % Version.jai
    val jamm           = "com.github.jbellis" %  "jamm"      % Version.jamm
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

def compilerSettings(sv: String) =
  CrossVersion.partialVersion(sv) match {
    case Some((2, 13)) =>
      Seq(
	"-deprecation",
	"-explaintypes",
	"-feature",
	"-language:higherKinds",
	"-unchecked",
	"-Xcheckinit",
	"-Xfatal-warnings",
	"-Xlint:adapted-args",
	"-Xlint:constant",
	"-Xlint:delayedinit-select",
	"-Xlint:doc-detached",
	"-Xlint:inaccessible",
	"-Xlint:infer-any",
	"-Xlint:missing-interpolator",
	"-Xlint:nullary-override",
	"-Xlint:nullary-unit",
	"-Xlint:option-implicit",
	"-Xlint:package-object-classes",
	"-Xlint:poly-implicit-overload",
	"-Xlint:private-shadow",
	"-Xlint:stars-align",
	"-Xlint:type-parameter-shadow",
	"-Ywarn-dead-code",
	"-Ywarn-extra-implicit",
	"-Ywarn-numeric-widen",
	"-Ywarn-unused:implicits",
	"-Ywarn-unused:imports",
	"-Ywarn-unused:locals",
	"-Ywarn-unused:params",
	"-Ywarn-unused:patvars",
	"-Ywarn-unused:privates",
	"-Ywarn-value-discard",
	"-Ycache-plugin-class-loader:last-modified",
	"-Ycache-macro-class-loader:last-modified",
      )
    case Some((2, 11)) =>
      Seq(
      "-deprecation",
      "-encoding", "UTF-8",
      "-explaintypes",
      "-feature",
      "-language:higherKinds",
      "-target:jvm-1.8",
      "-unchecked",
      "-Xcheckinit",
      "-Xfatal-warnings",
      "-Xfuture",
      "-Xlint:adapted-args",
      "-Xlint:by-name-right-associative",
      "-Xlint:delayedinit-select",
      "-Xlint:doc-detached",
      "-Xlint:inaccessible",
      "-Xlint:infer-any",
      "-Xlint:missing-interpolator",
      "-Xlint:nullary-override",
      "-Xlint:nullary-unit",
      "-Xlint:option-implicit",
      "-Xlint:package-object-classes",
      "-Xlint:poly-implicit-overload",
      "-Xlint:private-shadow",
      "-Xlint:stars-align",
      "-Xlint:type-parameter-shadow",
      "-Xlint:unsound-match",
      "-Ydelambdafy:method",
      "-Yno-adapted-args",
      "-Xmax-classfile-name", "78", // Workaround for SI-3623.
      "-Ypartial-unification",
      "-Ywarn-numeric-widen",
      "-Ywarn-unused-import",
      "-Ywarn-value-discard"
    )
    case _ =>
      Seq(
      "-deprecation",
      "-encoding", "UTF-8",
      "-explaintypes",
      "-feature",
      "-language:higherKinds",
      "-target:jvm-1.8",
      "-unchecked",
      "-Xcheckinit",
      "-Xfatal-warnings",
      "-Xfuture",
      "-Xlint:adapted-args",
      "-Xlint:by-name-right-associative",
      "-Xlint:constant",
      "-Xlint:delayedinit-select",
      "-Xlint:doc-detached",
      "-Xlint:inaccessible",
      "-Xlint:infer-any",
      "-Xlint:missing-interpolator",
      "-Xlint:nullary-override",
      "-Xlint:nullary-unit",
      "-Xlint:option-implicit",
      "-Xlint:package-object-classes",
      "-Xlint:poly-implicit-overload",
      "-Xlint:private-shadow",
      "-Xlint:stars-align",
      "-Xlint:type-parameter-shadow",
      "-Xlint:unsound-match",
      "-Ydelambdafy:method",
      "-Yno-adapted-args",
      "-Ypartial-unification",
      "-Ywarn-numeric-widen",
      "-Ywarn-unused-import",
      "-Ywarn-value-discard"
    )
  }

lazy val commonSettings =
  Seq(
    scalaVersion in ThisBuild := "2.13.1",
    crossScalaVersions := Seq(scalaVersion.value, "2.12.10", "2.11.12"),
    organization := "org.dfasdl",
    organizationName := "Wegtam GmbH",
    startYear := Option(2014),
    licenses += ("AGPL-V3", url("https://www.gnu.org/licenses/agpl.html")),
    headerLicense := Some(
      HeaderLicense.AGPLv3(s"2014 - $currentYear", "Contributors as noted in the AUTHORS.md file")
    ),
    scalacOptions ++= compilerSettings(scalaVersion.value),
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
  )
