# DFASDL Utils

[![Build Status](https://travis-ci.org/DFASDL/dfasdl-utils.svg?branch=master)](https://travis-ci.org/DFASDL/dfasdl-utils)
[![codecov](https://codecov.io/gh/DFASDL/dfasdl-utils/branch/master/graph/badge.svg)](https://codecov.io/gh/DFASDL/dfasdl-utils)
[ ![Download](https://api.bintray.com/packages/wegtam/dfasdl/dfasdl-utils/images/download.svg) ](https://bintray.com/wegtam/dfasdl/dfasdl-utils/_latestVersion)

The DFASDL is a language based upon [XML
Schema](http://www.w3.org/XML/Schema) that can be used to describe data
formats and additionally the semantics of it.

It is used by the Tensei-Data project to describe data structures and to
derive mappings and transformation functions between different structures
automatically.

This repository contains the utils module which provides helpful functions
and data types. It depends on the dfasdl-core package.

It is cross build for scala 2.11 and 2.12.

Releases are published on bintray and should be synced to jcenter. To use
the bintray repository directly just add the appropriate resolver to your
sbt configuration:

```
resolvers += "DFASDL" at "https://dl.bintray.com/wegtam/dfasdl"
```

The api documentation is published using github pages and is available 
online at: https://dfasdl.github.io/dfasdl-utils/

## System requirements

* Java 8
* Scala 2.12
* sbt

### Documentation

The documentation is generated via the sbt-site plugin. Use the `makeSite`
task in sbt to generate it.

### Tests

To execute the tests run the `test` task in sbt. Tests will be 
automatically run before publishing.

### Benchmarks

The project includes benchmarks which can be run via sbt:

```text
> benchmarks/jmh:clean
...
> benchmarks/jmh:compile
...
> benchmarks/jmh:run -i 10 -wi 4 -f3 -t1
...
```

Be sure to compile the whole project before.

