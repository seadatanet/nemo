Projet: NEMO
Code projet: 9064780

***********************************************************************
Objet: 
Generation de l'installer de NEMO au travers des outils

- IzPack 4.2 (http://www.izforge.com/izpack)
( site de dev izPack pour trouver la version 4.2: http://dist.codehaus.org/izpack/releases/)
 izPack sera cherché à l'emplacement suivant par le build.xml:
 C:\Program Files\IzPack
 
- ANT 1.7.1 (http://ant.apache.org)
***********************************************************************
Procédure: 

Mise à jour :
     fichier ant.properties (version et nom du projet)
     fichiers build.xml en particulier le chemin d'accès à IzPack
     fichiers install_<plate-forme>.xml en particulier "<appversion>1.0</appversion>" ainsi que les auteurs

Note: les 3 répertoires suivants doivent être créés :
- ressources/resources : contenant les resources du projet (mises à jour par le buid)
- dist: contient les installer une fois compilé
- src: contient les sources Eclipse à compiler (exécutables et ressources) pour les plates-formes suivantes:
+ linux.gtk.x86
+ linux.gtk.x86_64
// --- + solaris.gtk.sparc
+ win32.win32.x86

Note : le changement de version impacte dans NEMO_product le fichier plugin.properties

Note: certaines actions sont réalisées par du code java, appartenant au projet fr.ifremer.nemo.delete
Pour modifier ce code, 
- faire un checkout de ce projet
- modifier le code
- générer le jar (export)
- remplacer le jar dans /NEMO_IzPackSVN/ressources/deleteDirectory.jar

//-------------------------------------------------------------------------------------//
                               Procédure de Livraison NEMO
//-------------------------------------------------------------------------------------//

1/ incrémentation du numéro de version
- dans NEMO_product :
	plugin.properties
	plugin.xml
	
- dans NEMO_IzPack :
	ant.properties
	install_<plate-forme>.xml


2/ mise à jour des listes (via l'interface NEMO Options / Vocabulary update):

21/Ouvrir dans NEMO Options/ Vocabulary update
Laisser faire la mise à jour, qui met à jour:
-NEMO_product/resources/Vocab
-NEMO_product/resources/Edmo.xml
-NEMO_product/resources/Parameters.xml
-NEMO_product/resources/Survey.xml
La mise à jour de ces fichiers met à jour automatiquement le fichier NemoConfiguration.xml

22/ mettre à jour le fichier NemoConfiguration_ini.xml avec les bonnes valeurs des balises VocabListURL 
(les bonnes valeurs sont dans NemoConfiguration.xml qu'on vient de mettre à jour)






3/ remonter le tout sous CVS + Tag version (livraison_AAAA-MM-JJ_versionX-X-XX)


4/ sous NEMO_IzPack : supprimer le contenu des répertoires dist et src


5/ sous NEMO_product -> export wizard (nemo.product / overview)
- export for multiple platforms dans NEMO_IzPack/src
	linux (gtk / x86-64)
	linux (gtk / x86)
	solaris (gtk / sparc)
	win32 (win32 / x86)


Création de l'installer ; plusieurs possibilités
- via Eclipse : 
      lancer directement le build.xml
      
- en ligne de commande ANT : 
      se placer dans le répertoire contenant build.xml
      ant -f build.xml
      
- en ligne de commande IZ-Pack
     "C:\Program Files\IzPack\bin\compile.bat" install_<plate-forme>.xml -h "C:\Program Files\IzPack"
     
     
récupération des installeurs non windows sous linux (répertoires)
	install_nemo_<version>_linux
	install_nemo_<version>_linux64bits
	install_nemo_<version>_solaris


sous LINUX, pour chacun de ces répertoires :
- dos2unix launcher_nemo.sh

- compresser le répertoire
	tar cf install_nemo_<version>_<plate-forme>.tar install_nemo_<version>_<plate-forme>
	gzip install_nemo_<version>_<plate-forme>.tar


repasser sous WINDOWS
- placer la totalité des installeurs compressés sous siec
	9064780_conv/www/delivrables/CLIENT_versionXX_JJMMAAAA
	
- mettre à jour le fichier html pour accès
	9064780_conv/www/delivrables/delivrables.html
	
- mail de livraison envoyer à CONVMED .Exemple: 

	Bonjour Michèle,
 
	Tu trouveras la dernière version de NEMO (X.X.XX) sur le site http://projets.ago.fr/convmed/ dans "délivrables" (un installeur par environnement).
	 
	Cette nouvelle version comprend :
	- FAE XXXX <resumé>
	- FAE XXXX <resumé>
	- la mise à jour des ressources (listes Vocab)
	
	Cordialement,

