addSbtPlugin("org.foundweekends"  % "sbt-bintray"     % "0.5.1")
addSbtPlugin("de.heikoseeberger"  % "sbt-header"      % "5.4.0")
addSbtPlugin("pl.project13.scala" % "sbt-jmh"         % "0.2.27")
addSbtPlugin("com.typesafe.sbt"   % "sbt-ghpages"     % "0.6.2")
addSbtPlugin("com.typesafe.sbt"   % "sbt-git"         % "0.9.3")
addSbtPlugin("com.jsuereth"       % "sbt-pgp"         % "1.1.0")
addSbtPlugin("org.scalameta"      % "sbt-scalafmt"    % "2.3.1")
addSbtPlugin("org.scoverage"      % "sbt-scoverage"   % "1.6.1")
addSbtPlugin("com.typesafe.sbt"   % "sbt-site"        % "1.4.0")
addSbtPlugin("org.wartremover"    % "sbt-wartremover" % "2.4.3")

libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.7.25" // Needed by sbt-git

