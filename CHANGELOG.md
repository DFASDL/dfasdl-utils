# ChangeLog for the DFASDL utils package

All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](http://semver.org/).

## Conventions when editing this file.

Please follow the listed conventions when editing this file:

* one subsection per version
* reverse chronological order (latest entry on top)
* write all dates in iso notation (`YYYY-MM-DD`)
* each version should group changes according to their impact:
    * `Added` for new features.
    * `Changed` for changes in existing functionality.
    * `Deprecated` for once-stable features removed in upcoming releases.
    * `Removed` for deprecated features removed in this release.
    * `Fixed` for any bug fixes.
    * `Security` to invite users to upgrade in case of vulnerabilities.

## Unreleased

## 3.0.0 (2017-10-06)

### Added

- `DataElement` trait with subclasses for data type wrappers
- helper functions for data extraction from `DataElement`

### Changed

- element extractors use `DataElement` now instead of `Any`

## 2.0.0 (2017-08-31)

### Fixed

- extractor for num elements returns always decimal (even for `precision=0`)

## 1.0.0 (2017-06-29)

- initial public release

