addSbtPlugin("me.lessis"          % "bintray-sbt"     % "0.3.0")
addSbtPlugin("de.heikoseeberger"  % "sbt-header"      % "2.0.0")
addSbtPlugin("pl.project13.scala" % "sbt-jmh"         % "0.2.23")
addSbtPlugin("com.typesafe.sbt"   % "sbt-ghpages"     % "0.6.0")
addSbtPlugin("com.typesafe.sbt"   % "sbt-git"         % "0.9.3")
addSbtPlugin("com.jsuereth"       % "sbt-pgp"         % "1.0.1")
addSbtPlugin("org.scoverage"      % "sbt-scoverage"   % "1.5.0")
addSbtPlugin("com.typesafe.sbt"   % "sbt-site"        % "1.2.1")
addSbtPlugin("org.wartremover"    % "sbt-wartremover" % "2.1.1")

libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.7.25" // Needed by sbt-git

