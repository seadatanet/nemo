Copyright (C) 2006-2020 IFREMER, All right reserved.

Aim of NEMO is to convert ASCII data into specific SeaDataNet format (ODV, NetCDF or MedAtlas).

Three types of data can be processed :
* Vertical profiles (measured at a fixed location, from the surface to the bottom of the sea, reference parameter: pressure or depth)
* Times series (measured at a fixed location and depth over a time period,reference parameter: timestamp)
* Trajectories (measured underway along the ship track, reference parameters: latitude and longitude).

Data are gathered during cruises. Data are available in ASCII files (one file 
per station or one file for all stations). NEMO allows to configure and  process
a great number of input format in order to produce file in ODV, NetCDF or MedAtlas formats.

Informations concerning a station must be located at the same place for all stations,
at the same line and same column in the file. Location of Metadata and data must be unchanged from a station
to the other.

NEMO does not support Excel, Word, LibreOffice, ... format as input files, only text file are accepted.
