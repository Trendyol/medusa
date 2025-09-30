# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.13.0] - 2025-29-09

### Added
- Add support to observe fragment transactions
- Add functionality to get pending or current fragment

### Changed
- Bump Java compatibility from 17 to 21

## [0.12.1] - 2024-10-02

### Fixed
- Fix a bug where a root fragment is not being displayed after we call resetCurrentTab by @MertNYuksel in #57

## [0.12.0] - 2024-06-27

### Added

- This changelog file
- Fix a bug where a fragment that we want to hide is not added to the fragment manager,
causing an IllegalStateException due to multiple fragment transactions occurring simultaneously 
because of the executePendingTransactions() method call

### Changed

- Increased minimum supported SDK from 15 to 21
- Increased compile and target SDK from 32 to 34
- Updated Kotlin from 1.7.20 to 2.0.0
- Updated Gradle from 7.6.1 to 8.8
- Updated Android Gradle Plugin from 7.4.1 to 8.5.0
- Updated Robolectric from 4.9.2 to 4.12.2
- Updated Truth from 1.1.3 to 1.4.2
- Updated Fragment testing from 1.5.5 to 1.8.0
- Updated arch. core testing from 2.1.0 to 2.2.0
- Updated Gradle Publish plugin from 1.1.0 to 2.0.0
- Updated GitHub Actions dependencies 
