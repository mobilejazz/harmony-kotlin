# CHANGELOG

# Change Log

## [5.0] - 2022-08-12
### Breaking Changes
- Update dependencies ([#94](https://github.com/mobilejazz/harmony-kotlin/pull/94))
  - Requires [migrating Ktor](https://ktor.io/docs/migrating-2.html) & [enabling new memory manager](https://github.com/JetBrains/kotlin/blob/master/kotlin-native/NEW_MM.md#switch-to-the-new-mm)

### Added
- Added RetryDataSource ([#90](https://github.com/mobilejazz/harmony-kotlin/pull/90))
- Added VoidLogger ([#89](https://github.com/mobilejazz/harmony-kotlin/pull/89))

### Changed
- Added JVM & Android code sharing ([#93](https://github.com/mobilejazz/harmony-kotlin/pull/93))

## [4.0] - 2022-05-10
### Breaking Changes
- Simplification of errors ([#83](https://github.com/mobilejazz/harmony-kotlin/pull/83))

### Added
- Added support for Interactors returning Either objects ([#84](https://github.com/mobilejazz/harmony-kotlin/pull/84))
- Added mappers from ByteArray to Object and vice-versa ([#80](https://github.com/mobilejazz/harmony-kotlin/pull/80))
- Added PresenterViewHolder to remove the view reference on Presenters automatically ([#79](https://github.com/mobilejazz/harmony-kotlin/pull/79))

### Changed
- Updated dependencies ([#86](https://github.com/mobilejazz/harmony-kotlin/pull/86))

## [3.1.0] - 2022-03-24
### Added
- DataSourceQueryMapper. Provide a way to map queries for custom datasource. Domain queries are recommended. ([#77](https://github.com/mobilejazz/harmony-kotlin/pull/77))
- SqlDelight queries extensions to execute queries with DataNotFoundException. If result is empty, then throw DataNotFoundException.

## [3.0.2] - 2022-03-10
### Added
- Added support for POST Http Request with empty body ([#74](https://github.com/mobilejazz/harmony-kotlin/pull/74))

## [3.0] - 2021-12-16
### Breaking Changes
- Improvements on error handling ([#64](https://github.com/mobilejazz/harmony-kotlin/pull/64))
- Changed CacheSyncOperation fallback function to accept two arguments (network & cache errors) ([#60](https://github.com/mobilejazz/harmony-kotlin/pull/60))

### Changed
- Added support for kotlin 1.6.10 & migrated build.gradle to kotlin-dsl ([#67](https://github.com/mobilejazz/harmony-kotlin/pull/67))

## [2.0.1] - 2021-11-16
### Added
- Added Type utilities to generate random values for the test suite ([#52](https://github.com/mobilejazz/harmony-kotlin/pull/52))

# [2.0] - 2021-10-21
- First KMM release
