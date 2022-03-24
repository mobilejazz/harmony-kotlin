# CHANGELOG

# Change Log

## [3.1.0] - 2022-03-24
### Added
- DataSourceQueryMapper. Provide a way to map queries for custom datasource. Domain queries are recommended.
- SqlDelight queries extensions to execute queries with DataNotFoundException. If result is empty, then throw DataNotFoundException.

## [3.0.2] - 2022-03-10
### Added
- Added support for POST Http Request with empty body

## [3.0] - 2021-12-16
### Changed
- Added support for kotlin 1.6.10 & migrated build.gradle to kotlin-dsl (#67)
- Improvements on error handling (#64)
- Changed CacheSyncOperation fallback function to accept two arguments (network & cache errors) (#60)

## [2.0.1] - 2021-11-16
### Added
- Added Type utilities to generate random values for the test suite (#52)


# [2.0] - 2021-10-21
- First KMM release
