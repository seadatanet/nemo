concernant les fichiers bioodv_432515 et Dataset.csv, fournis par OGS, les informations sont :
As for ODV Bio, I'm attaching Dataset.csv which is a user-friendly output format based on Darwin-Core terms. As input for NEMO ODV bio creation we would:
1. Change occurrenceStatus value to 0 and 1.
2. Change column names to the P01 codes.
3. Add required metadata rows before the table.
4. Separate to different files based on combination of locationID and depth.
5. Use a folder as input for creating multiple ODV.
Maybe it helps having also an example of a BioODV we currently produce (time series): odv_data/bioodv_432515.txt






