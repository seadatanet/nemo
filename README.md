# NEMO Software (ASCII marine data conversion tool for [SeaDataNet formats](https://archimer.ifremer.fr/doc/00454/56547/))

![NEMO Software logo](figures/nemo.jpg "NEMO logo")

[![DOI](https://img.shields.io/badge/DOI-10.12770%2F62112611--7ab2--4c10--9e46--c440663a55ac-blue)](https://doi.org/10.12770/62112611-7ab2-4c10-9e46-c440663a55ac)
[![License: LGPL-3.0-only](https://img.shields.io/badge/License-LGPL--3.0--only-blue.svg)](https://spdx.org/licenses/LGPL-3.0-only.html)
[![Current stable version](https://img.shields.io/badge/stable-2.2.0-blue)](https://www.seadatanet.org/Software/NEMO/)

<!-- Enable this dynamic badge once GitHub Releases reliably reflects official stable releases:
[![Latest stable release](https://img.shields.io/github/v/release/seadatanet/nemo?label=latest%20release)](https://github.com/seadatanet/nemo/releases/latest)
-->

## Persistent identifiers

- **DOI**: [10.12770/62112611-7ab2-4c10-9e46-c440663a55ac](https://doi.org/10.12770/62112611-7ab2-4c10-9e46-c440663a55ac) — persistent identifier for citing NEMO.
- **SWHID**: a version-specific identifier for the source code archived in [Software Heritage](https://archive.softwareheritage.org/) will be added when the NEMO `2.2.0` release has been tagged and archived. No SWHID for `2.2.0` is claimed yet.

<!-- Once NEMO 2.2.0 has been released and archived, replace the SWHID line above with
     the verified swh:1:dir:... identifier and link to its Software Heritage permalink.
     Do not use the SWHID of the development branch as a release identifier.
     Optionally add a Software Heritage badge, as in the OCTOPUS README. -->

## Citation

Please use the software DOI provided above when citing NEMO.

Detailed citation metadata are available in the [`CITATION.cff`](CITATION.cff) file.

## Latest stable release

**NEMO 2.2.0** is the latest official release.

- **[Download NEMO](https://www.seadatanet.org/Software/NEMO/)** — official SeaDataNet distribution packages.
- **[Changelog](changelog.md)** — release history and detailed changes.

Key evolutions in **2.2.0** include:

- Migration of the NEMO client and related web services to **Java 11 (LTS)**.
- Updated Eclipse RCP platform and build dependencies, including OCTOPUS 1.12.0 libraries.
- Updated Windows and Linux installers for **64-bit** Java 11 environments.
- Docker support for NEMO and Nautilus web services.
- Dynamic consumption of SeaDataNet external resources at build time.
- FAIR/open-science metadata and documentation improvements, with release-specific Software Heritage identification to be completed after archiving.
- Fixes for remote update checks and build/packaging stability.

See the [changelog](changelog.md) for full details.

## FAIR software status

FAIR software assessment and metadata improvements are integrated into the 2.2.0 release. The presence of DOI and citation metadata does not imply that Software Heritage archival of 2.2.0 has already taken place (archival follows the official tag).

## Overview

NEMO is a SeaDataNet software tool written in Java and designed to support marine data centres in the harmonisation and conversion of heterogeneous marine data files into SeaDataNet standard exchange formats.

As part of the SeaDataNet CDI data discovery and access services, marine datasets are distributed using common transport formats interoperable with SeaDataNet standards, controlled vocabularies and associated analysis and visualisation tools such as ODV and DIVA.

Depending on their local data management configurations, data centres may need to preprocess in-house datasets before integration into SeaDataNet workflows. To support this process, NEMO enables conversion from heterogeneous ASCII formats into:

- SeaDataNet ODV format
- SeaDataNet MedAtlas ASCII format
- SeaDataNet NetCDF (CF) format

NEMO supports marine time series, profiles and trajectory observations.

The software also relies on [SeaDataNet Common Vocabularies](https://vocab.seadatanet.org/search) and the [EDMO directory](https://edmo.seadatanet.org/), which can be synchronised through dedicated online web services.

NEMO contributes to the interoperability, harmonisation and FAIRification of marine observation data within the SeaDataNet infrastructure.

## Contributors

Project Supervisor:

- [Steven PIEL](https://github.com/spiel-ifremer), [Ifremer](https://edmo.seadatanet.org/report/486), Brest, France, [ORCID:0009-0008-1759-5254](https://orcid.org/0009-0008-1759-5254)

Developers:

- Sébastien CROUZILLE, Capgemini Engineering, Brest, France
- Pierre MAHOUDO, Capgemini Engineering, Brest, France

Current Contributors:

- Vincent FACHERO, [Ifremer](https://edmo.seadatanet.org/report/486), Brest, France
- Julie GATTI, [Ifremer](https://edmo.seadatanet.org/report/486), Brest, France, [ORCID:0000-0001-9327-4366](https://orcid.org/0000-0001-9327-4366)
- Françoise LE HINGRAT, [Ifremer](https://edmo.seadatanet.org/report/486), Brest, France
- Cécile PERTUISOT, [Ifremer](https://edmo.seadatanet.org/report/486), Brest, France, [ORCID:0000-0002-5474-2224](https://orcid.org/0000-0002-5474-2224)
- Marine VERNET, [Ifremer](https://edmo.seadatanet.org/report/486), Brest, France, [ORCID:0000-0002-3906-5579](https://orcid.org/0000-0002-3906-5579)

Past Contributors:

- Sophie BREGENT, [Ifremer](https://edmo.seadatanet.org/report/486), Brest, France
- Michèle FICHAUT, [Ifremer](https://edmo.seadatanet.org/report/486), Brest, France, [ORCID:0000-0001-5900-6149](https://orcid.org/0000-0001-5900-6149)
- Leo LE LONQUER, [Ifremer](https://edmo.seadatanet.org/report/486), Brest, France
- Vanessa TOSELLO, [Ifremer](https://edmo.seadatanet.org/report/486), Brest, France, [ORCID:0009-0009-7337-5245](https://orcid.org/0009-0009-7337-5245)

## Software metadata

### Domain

Physical oceanography

### Support

For any support or question please contact [sdn-userdesk@seadatanet.org](mailto:sdn-userdesk@seadatanet.org)

### Funders

- [Ifremer](https://edmo.seadatanet.org/report/486)
- [European Commission](https://commission.europa.eu/index_en) under the [Horizon 2020](https://research-and-innovation.ec.europa.eu/funding/funding-opportunities/funding-programmes-and-open-calls/horizon-2020_en) (SeaDataNet projects: SeaDataNet, SeaDataNet 2, SeaDataCloud)

### Languages

- English
- French

### Programming languages

Java

### Project history

NEMO has been developed within the SeaDataNet infrastructure since at least June 2007 to support harmonisation and conversion of marine observation datasets into SeaDataNet standard exchange formats.

### Keywords

SeaDataNet, NEMO, marine data, oceanographic data, ASCII, ODV, MedAtlas, NetCDF, CF conventions, CDI, marine observations, data conversion, interoperability, FAIR data, controlled vocabularies, EDMO, oceanography, marine data management

### Machine-readable metadata

Machine-actionable software metadata are provided in [`codemeta.json`](codemeta.json) in the development repository; citation metadata are provided in [`CITATION.cff`](CITATION.cff).

Release history, including the forthcoming 2.2.0 changes, is documented in [`changelog.md`](changelog.md).

## Requirements, Supported Environments, Installation

### Requirements

NEMO is written in Java.

Requirements for current and legacy versions:

| Version | Java requirement | Development/build environment |
| --- | --- | --- |
| **2.2.0** (current stable) | Java **11 (LTS)** | Eclipse RCP with Java 11 target platform; Maven / GitLab CI |
| **2.1.1** (legacy) | Java JDK **1.8** | Maven; Eclipse RCP Photon 2018-12 (4.10.0) |

### Supported Environments

NEMO is primarily developed and used on Windows and Linux environments (64-bit).

The software supports the processing and conversion of marine observation datasets including:

- time series
- profiles
- trajectories

### Installation

Precompiled NEMO distributions are available from the SeaDataNet portal:

[Download NEMO](https://www.seadatanet.org/Software/NEMO/).

Choose the package appropriate for your system (Windows or Linux 64-bit with Java 11).

## Get started

NEMO is designed to support marine data centres in converting heterogeneous ASCII marine datasets into SeaDataNet standard exchange formats.

The software makes use of:

- SeaDataNet Common Vocabularies
- EDMO directory services
- mapping and configuration files
- SeaDataNet CDI workflows

For operational use and configuration details, please refer to the associated SeaDataNet documentation and user manuals.

For settings and details of all other available functions, please refer to the [user manual](https://www.seadatanet.org/content/download/7899/file/sdn_Nemo_UserManual.pdf).

## Development & Build Guide

### Build en local

#### 1. Configurer les tokens Maven

Copier le fichier `ci_settings.xml` en `local-settings.xml` **à la racine du dépôt** (ce fichier est gitignored) et remplacer les variables par les vraies valeurs :

| Variable | Description |
|---|---|
| `${env.DEPLOY_TOKEN_INTERNAL}` | Deploy token du projet GitLab 1226 (dépendances internes IFREMER) |
| `${env.DEPLOY_TOKEN_NEMO}` | Deploy token du projet GitLab 2603 (registry NEMO) |
| (voir ci-dessous) | Personal Access Token pour les ressources SeaDataNet |

Pour les tokens `DEPLOY_TOKEN_*`, voir le fichier `liste_tokens` hors repo.

#### 2. Ajouter l'accès aux ressources SeaDataNet externes

Dans `local-settings.xml`, ajouter un bloc serveur pour le registry externalresources (projet GitLab 2512).
Ce serveur utilise un **Personal Access Token** GitLab (scope `read_api`) :

```xml
<server>
  <id>gitlab-maven-externalresources</id>
  <configuration>
    <httpHeaders>
      <property>
        <name>Private-Token</name>
        <value>VOTRE_PERSONAL_ACCESS_TOKEN</value>
      </property>
    </httpHeaders>
  </configuration>
</server>
```

#### 3. Récupérer les ressources SeaDataNet (après chaque checkout)

Les fichiers `Edmerp.xml`, `edmo.xml` et `externalResources/` ne sont plus commités dans git.
Ils sont téléchargés depuis le registry GitLab 2512 à la première phase `generate-resources` :

```powershell
# Depuis fr.ifremer.nemo/
mvn.cmd process-resources -f pom.xml -s ..\local-settings.xml
```

Cette étape est automatiquement incluse dans tout build complet (`compile`, `install`, etc.).

#### 4. Compiler le projet

```sh
# Pour compiler l'intégralité du projet
cd fr.ifremer.nemo.parent
mvn clean install -DskipTests -s ../local-settings.xml

# Pour compiler une partie du projet
cd <dossier du module>
mvn clean install -DskipTests -s ../local-settings.xml
```

Si certaines dépendances ne peuvent pas être téléchargées, vérifier les configurations de tokens et réessayer avec `-U` :

```sh
mvn -U clean install -DskipTests -s ../local-settings.xml
```

### Build en distant (gitlab-ci)

Le fichier `.gitlab-ci.yml` fait foi.

### Mise en place de l’environnement de développement

#### Java & Eclipse

- **JDK 11** (OpenJDK Temurin 11 recommandé)
- **Eclipse IDE for RCP and RAP Developers** (ou Photon avec target platform Java 11)

#### Import et configuration du workspace Eclipse

1. **Récupérer les sources :**
   ```sh
   git clone https://gitlab.ifremer.fr/seadatanet/applications/nemo.git
   ```
2. **Récupérer les ressources SeaDataNet :**
   ```powershell
   # Depuis fr.ifremer.nemo/
   mvn.cmd process-resources -f pom.xml -s ..\local-settings.xml
   ```
3. **Configurer la Target Platform :**
   - Importer tous les projets Maven dans Eclipse
   - Ouvrir `fr.ifremer.nemo.tp/fr.ifremer.nemo.tp.target` et cliquer sur **"Set as Active Target Platform"**
4. **Lancer l'application :**
   Des configurations de lancement `.launch` sont disponibles dans `fr.ifremer.nemo/` (clic droit → Run As / Debug As).

### Procédure de release

Consulter le document [`RELEASE.md`](RELEASE.md) pour la checklist détaillée de release (incrémentation de version, IzPack, conteneurs Docker webservices, tags Git et publication CI/CD).

## License

[GNU Lesser General Public License v3.0 only](https://spdx.org/licenses/LGPL-3.0-only.html)