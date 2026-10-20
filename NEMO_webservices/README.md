

# Parties client et serveur de webservices utilisés par Nemo.

## Artefacts produits par le projet ws


 - ws_client -> (jar) Librairie client java a utiliser par l'application Nemo
 
 - ws_common -> (jar) Code commun aux deux webservices
 
 - ws_nautilus -> (nautilus.war) webservice des données Nautilus
 
 - ws_nemo -> (nemo.war) webservice des donnée nemo

## Compilation

Dans le répertoire racine du projet (`NEMO_webservices`) :

```
./mvnw clean package
```

## Déploiement serveur

les deux fichiers war ( ws_nautilus/target/nautilus.war et ws_nemo/target/nemo.war) doivent être déployés sur un serveur qui déclare une source de donnée JNDI nommée 'SISMER'


NemoService service = new NemoServiceClient("http://serveur/nemo/api");
...
List<Country> countries = service.getCountries();
