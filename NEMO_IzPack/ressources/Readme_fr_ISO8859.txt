Copyright (C) 2006-2020 IFREMER, All right reserved.

NEMO a pour objet de mettre à  un format SeaDataNet (format ODV, NetCDF ou MedAtlas) des données 
reçues à  un format ASCII quelconque. 

Ces données peuvent être de 3 types :

* Profils verticaux (mesures en un point fixe de la surface vers le fond de la mer, paramètre de référence : Pression ou profondeur)
* Séries temporelles (Mesures en un point fixe et Ã  une profondeur donnée pendant une période de temps, paramètre de référence : date, heure)
* trajectoires (Mesures le long de la route du navire, paramètres de référence : latitude et longitude)

Les données ont été mesurées lors de campagnes en mer. Elles arrivent sous
forme de fichiers ASCII=texte (un fichier par station ou bien un fichier pour toutes
les stations d'une campagne en mer ou d'un mouillage). Les formats des fichiers
reçus sont très variés, NEMO permet de configurer et traiter le plus grand
nombre de formats possible.

Le principe est d'offrir la possibilité pour l'utilisateur de décrire le format
du ou des fichiers en entrée, pour pouvoir ensuite y trouver l'information
nécessaire Ã  la génération des fichiers ODV, NetCDF ou MedAtlas.

Il est entendu que dans les fichiers en entrée, les informations concernant les
stations d'une campagne se trouvent à  la même position (même ligne, même rang)
pour toutes les stations. Le format des méta-données et des données ne change
pas non plus d'une station à  l'autre. La description doit être homogène. 

NEMO ne prend pas en entrée des fichiers de type Excel, Word, LibreOffice ... 
Seuls les fichiers texte peuvent être pris en compte.


