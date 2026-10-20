# Changelog

All notable changes to NEMO are documented in this file.
The format is inspired by Keep a Changelog and Semantic Versioning.

---

## 2.2.0 (2026-XX-XX)

### Enhancements / Évolutions

* **Java 11 Migration**: Migrated NEMO client and associated web services to **Java 11 (LTS)**.
* **Platform & Dependencies**:
  * Updated Eclipse RCP target platform and build system.
  * Included Octopus 1.12.0 libraries.
  * Updated Windows and Linux installers for 64-bit Java 11 runtime.
* **Web Services**: Containerized NEMO and Nautilus web services with Docker support.
* **FAIR Compliance & Open Science**:
  * Added machine-actionable metadata (`codemeta.json`) and citation metadata (`CITATION.cff`).
  * Assigned a persistent DOI and enabled Software Heritage archival (SWHID).
  * Clarified GNU LGPL v3 licensing across documentation, metadata, and application About dialog.
  * Overhauled project documentation (`README.md`, `CHANGELOG.md`).

### Bug Fixes / Corrections

* Fixed software version check failure when the remote update service is unreachable.
* Various stability, build and packaging improvements under Java 11.

## 2.1.2 (2026-01-22)

### Bug Fixes / Corrections

* Fixed a problem with station comments in CSV files.

## 2.1.1 (2024-07-31)

### Bug Fixes / Corrections

* Fixed a regression causing station duplication in output files.
* Avoided special characters in CDI Summary files to preserve CSV compatibility.
* Added min/max Z values in CDI Summary files for profile datasets.
* Fixed unit update issues in the Data tab.
* Accepted "." as a valid separator for date and time input values.
* Accepted variable-length station codes.
* Automatically selected the data type when only one option is available.
* Fixed a regression affecting data parsing in Autoline widgets.

## 2.1.0 (2024-05-21)

### Enhancements / Évolutions

* Included Octopus 1.10.0 libraries.
* Added separate encoding settings for input and output formats.
* Added an additional parameters configuration file for NetCDF output.

## 2.0.1 (2023-05-05)

### Bug Fixes / Corrections

* Fixed a regression affecting non-CSV files introduced by the new CSV parser.

## 2.0.0 (2023-04-20)

### Enhancements / Évolutions

* Major release focused on improving usability and data conversion workflows.
* Added drag-and-drop support in parameter tables.
* Added multi-selection support for instruments, default values, formats and units.
* Added automatic parameter detection for CSV files.
* Added automatic start/end position detection for measured parameters and quality flags.
* Enriched CDI Summary files used by MIKADO.
* Added EDMERP and curve auxiliary output files.
* Added the BatchBuilder tool to generate .bat and .sh scripts.
* Added multi-line header selection in the File tab.
* Added automatic detection of lines containing P01 or P09 terms.
* Replaced project descriptions with EDMERP codes in the Cruise tab.

## 1.7.4 (2022-05-31)

### Enhancements / Évolutions

* Added support for BODV, MODV and SODV format versions in CDI Summary files.
* Included Octopus 1.8.0 libraries with SODV support.
* Updated CSR service URLs.
* Added CSR update status information in the update window.
* Updated several labels.

### Bug Fixes / Corrections

* Various maintenance and stability improvements.

## 1.7.3 (2022-01-18)

### Enhancements / Évolutions

* Added SDN QC flag "B" (nominal value).

### Bug Fixes / Corrections

* Fixed CSV to MEDATLAS conversion issues.
* Updated Log4j to version 2.17.1.
* Included Octopus 1.7.1 libraries addressing Log4j vulnerabilities.
* Fixed trajectory sorting issues in ODV and MEDATLAS formats.

## 1.7.2 (2021-10-14)

### Enhancements / Évolutions

* Introduced the new BODV and MODV naming conventions in coupling tables.

### Bug Fixes / Corrections

* Updated Ifremer cruise web services to REST.
* Added support for unique LOCAL_CDI_ID values for shipborne ADCP trajectories.
* Improved memory management for large files.
* Fixed several typos.

## 1.7.1 (2021-02-05)

### Enhancements / Évolutions

* Added the -cruiseComment batch argument to populate DATASET_ABS in CDI Summary files.

### Bug Fixes / Corrections

* Fixed a regression affecting the -datasetRef batch option.

## 1.7.0 (2020-03-15)

### Enhancements / Évolutions

* Added support for biological, flow cytometry and microlitter ODV variants.
* Allowed multiple parameters sharing the same P01 code.
* Enabled vocabulary updates in batch mode.
* Added support for separators in station headers.
* Added batch arguments for cruise metadata.
* Added a new CSV termination indicator based on column value changes.
* Added support for ISO and Julian date formats.
* Added manual data type definition in station descriptions.
* Added support for date parameters in data descriptions.
* Updated EDMO, CDI, CSR and vocabulary services.

### Bug Fixes / Corrections

* Fixed manual time entry issues for time series.
* Fixed French labels.
* Fixed handling of some NA values.
* Reduced memory issues with large files.
* Improved handling of text parameters and model backups.

## 1.6.7 (2019-01-21)

### Enhancements / Évolutions

* Added support for BSH CSR references in SeaDataNet references.
* Improved the flag scales window in NEMO settings.
* Added the "decimal degrees for file with separators" coordinates format in the Station tab.
* Changed the SDN NetCDF output fill value to `-99999`.
* Added the SDN QC flag "Q" for values below the limit of quantification.
* Updated the SeaDataNet CSR XML Schema to version 4.0.0.

### Bug Fixes / Corrections

* Improved compatibility with Java 9 and later versions.
* Fixed an issue preventing NEMO from starting when the version service was unreachable.
* Fixed incorrect input flag values for CSV files in the Data tab.
* Fixed incorrect whitespaces in ODV output files.
* Improved data column widths in the Data tab.
* Restored the possibility of switching back to manual date and time input after automatic input had been selected.
* Fixed missing ships in the Cruise tab when several ships shared the same name.

## 1.6.6 (2017-11-22)

### Bug Fixes / Corrections

* Fixed an issue preventing NEMO from starting when the version service was unreachable.
* Fixed incorrect input flag values for CSV files in the Data tab.

## 1.6.5 (2017-09-27)

### Bug Fixes / Corrections

* Fixed incorrect highlight synchronization between the data table and the parameter table for CSV files.
* Fixed an incorrect P06 unit check for non-numeric parameters in the Data tab.

## 1.6.4 (2017-08-23)

### Enhancements / Évolutions

* Added a table in the Data tab to display and select parameters for CSV input files.
* Added detection of deprecated or non-existing BODC terms in the Cruise, Station and Data tabs, in both GUI and batch modes. Deprecated terms are automatically replaced when a replacement code is provided by BODC.
* Added software version checking.

### Bug Fixes / Corrections

* Fixed data format issues with large integers and floating-point values in the Data tab.
* Forced incremental station numbering for trajectories when station numbers could not be read from station headers.
* Fixed date computation in MedATLAS and CFPoint time series conversions when using a user-defined offset.
* Fixed start date and sampling rate handling for multi-station MedATLAS time series.
* Fixed end date computation for unordered multi-station MedATLAS time series with incremented station numbers.
* Fixed ODV profile conversion when pressure is available without depth and the pressure format is integer.
* Improved processing time for CFPoint conversions.
* Fixed the format of `SDN_CRUISE` `start_date` and `end_date` attributes in CFPoint files to use `YYYY-MM-DD`.
* Fixed P06 unit selection in the Data tab when using P01 via P09.
* Fixed ISO date/time column headers in ODV conversions.

## 1.6.3 (2016-05-19)

### Bug Fixes / Corrections

* Fixed date and time handling for trajectory conversions when dates and times are read from the input file.
* Fixed an incorrect title in the L22 selection window.

## 1.6.2 (2016-01-04)

### Enhancements / Évolutions

* Improved parameter and unit selection tables.

### Bug Fixes / Corrections

* Fixed handling of files containing textual data using P01 via P02 BODC parameters.
* Fixed issues related to the `Summary_cdi_nemo.xml` file.
* Fixed station ordering in output files by sorting stations first by date and then by station number.

## 1.6.1 (2015-07-15)

### Bug Fixes / Corrections

* Fixed coupling table import and editing issues.

## 1.6.0 (2015-06-08)

### Enhancements / Évolutions

* Added automatic SeaDataNet references for CDI, CSR and ship information when the corresponding metadata are available.
* Renamed the Cruise tab to Cruise/Collection and made it always visible.
* Used the cruise name in the ODV Cruise column when available, otherwise the cruise reference is used.
* Added `instrument` and `fall_rate` attributes to the SDN mapping in the Data tab.
* Added support for DEPH as primary variable for vertical profiles in MEDATLAS.
* Added Server Initialisation and XML Initialisation for ODV conversions.
* Added support for textual ODV data using `INDEXED_TEXT` column headers.

### Bug Fixes / Corrections

* Fixed the P06 code used for non-numeric ODV data units by using `UUUU` instead of `UKNW`.
* Fixed numerical sorting of data within stations by increasing reference parameter.
* Fixed coupling table updates in batch mode.
* Fixed incorrect station numbers when converting trajectory collections to NetCDF.
* Fixed a generation error when adding a parameter in the Data tab after a first conversion.
* Fixed CFPoint conversion of time series using a start date and sampling rate.
* Fixed the default quality flag on the `TIME` variable in CFPoint files.

## 1.5.4 (2014-06-06)

### Bug Fixes / Corrections

* Fixed an empty ship list in the Cruise tab on Linux.
* Fixed the `Conventions` global attribute label to use `SeaDataNet_1.0 CF 1.6` instead of `SeaDataNet_1.0 CF-1.6`.
* Updated unit labels in NetCDF CFPoint files from British English to US English, for example `metre` to `meter`.

## 1.5.3 (2014-03-31)

### Enhancements / Évolutions

* Added support for reading CSR ISO 19139 records in the Cruise tab.
* Added a validation step during SDN mapping generation: conversion is cancelled when no mapping is found for a parameter or unit.

### Bug Fixes / Corrections

* Improved EDMO selection in NEMO settings.
* Fixed incorrect updates of the Data section when modifying an existing model.
* Fixed missing default-value lines in some particular MEDATLAS files.
* Revised `SDN_CRUISE` attributes for cruise collections in NetCDF format.

## 1.5.2 (2013-09-16)

### Enhancements / Évolutions

* Added `FORMAT_VERSION` support for CDI Summary files.

### Bug Fixes / Corrections

* Fixed handling of relative paths for coupling tables.

## 1.5.1 (2013-07-18)

### Enhancements / Évolutions

* Replaced the C16 list with the C19 list while preserving backward compatibility with existing models.

## 1.5.0 (2013-07-18)

### Enhancements / Évolutions

* Added support for conversion to NetCDF format.
* Added compatibility with version 2 of the BODC vocabularies.
* Added support for measurements expressed above sea level.

## 1.4.5 (2012-07-11)

### Bug Fixes / Corrections

* Fixed the end date of time series with non-constant sampling rates.
* Fixed file address handling in coupling tables.
* Fixed default value handling in the ODV format.

## 1.4.4 (2012-03-21)

### Enhancements / Évolutions

* Added support for non-numeric parameters.
* Added support for time series with non-constant sampling rates.

## 1.4.3 (2012-01-24)

### Enhancements / Évolutions

* Added support for keeping previous installation preferences when reinstalling NEMO.
* Added the possibility to save incomplete models.
* Added a new example for batch mode script files.
* Added support for several input and output files for trajectories.
* Marked mandatory fields with an asterisk (`*`) on input screens.
* Made the output default value optional in Data descriptions for ODV format.

## 1.4.0 (2011-05-12)

### Enhancements / Évolutions

* Added a user label column to parameter tables.
* Added file backup support.
* Added column usage configuration.
* Added a data termination indicator.
* Updated the measurement table menu with options to:
  * select a format;
  * move the current parameter to the top;
  * move the current parameter to the bottom.
* Added new batch mode arguments:
  * `couplingTableUpdate`
  * `coupingTablePrefix`

## 1.3.0 (2011-02-18)

### Enhancements / Évolutions

* Added support for P011 parameters through P021.
* Added new parameter lists.
* Moved conversion format selection to the File tab.
* Updated the Depth section in the Station tab.
* Updated the Data tab.
* Updated the Convert tab.
* Added information about parameters automatically inserted by NEMO.
* Added new batch mode arguments for CDI Summary and coupling table generation.

## 1.2.25 (2010-05-25)

### Enhancements / Évolutions

* Added global validation of all NEMO steps.
* Added batch mode support.
* Added support for P011 parameter codes.
* Added a default value for bottom depth.
* Added a default value for station start time.
* Added a NEMO setting to choose the default quality flag.
* Reworked the source code to improve conversion of large files containing several thousand stations.

### Bug Fixes / Corrections

* Fixed several anomalies.

## 1.2.16 (2009-11-26)

### Enhancements / Évolutions

* Modified the size of the Settings windows.

## 1.2.14 (2009-07-15)

### Enhancements / Évolutions

* Added an installation procedure for Linux and Unix.

> Note: this installation procedure was not tested at Ifremer.

## 1.2.12 (2009-06-04)

### Bug Fixes / Corrections

* Applied minor corrections to time and latitude descriptions.

## 1.2.11 (2009-05-25)

### Enhancements / Évolutions

* Added reporting when updating vocabularies.
* Added a Restore button in the NEMO configuration.
* Added a rupture indicator for station headers.
* Added support for chronological Julian dates in input files.
* Added support for multi-station ODV files as input.

## 1.2.0 (2009-03-13)

### Enhancements / Évolutions

* Added SeaDataNet extensions for trajectories in ODV and MEDATLAS formats.
* Added support for preserving input file quality control flags.
* Added CDI Summary generation for use with MIKADO.
* Added coupling table generation.

## 1.13 (2008-11-01)

### Enhancements / Évolutions

* Added conversion of quality flags.
* Added specific comment lines for parameters.
* Added generation of an Excel file usable by MIKADO to create CDI XML files.

## 1.11 (2008-05-07)

### Enhancements / Évolutions

* Added support for files using separators.
* Added conversion of collections of files not related to a cruise.

## Earlier releases

NEMO was already available to SeaDataNet users in 2007.
Detailed release information prior to version 1.11 (2008-05-07)
has not yet been recovered.
