## Aufgabe  

Die Fluggesellschaft möchte den Route Service nun an eine MySQL Datenbank anbinden. 
Die Datenbankmigration soll über ein Task App, basierend auf **Flyway** und **Spring Cloud Task**, 
durchgeführt werden. Die Task App soll über einen Cloud Task aufgerufen werden. 


1. MySQL Cloud Service erstellen 
2. Task App für Datenmigration erstellen 
3. Cloud Task auf Task App ausführen 



## Login 
**Project: cna-pas-springio-data-start**


```
cf login -a api.run.pivotal.io -u <user> -p <password> 

```

## MySQL Datenbank erstellen  
**Project:  cna-pas-springio-data-start**


Suchen Sie den richtigen MySQL Cloud Service aus dem Marketplace aus und installieren Sie diesen. 
Achten Sie darauf, dass die Installation im hintergrund durchgeführt wird und ein weile dauern kann. 
Ueberpruefen Sie den Status der Installation. 


```
cf marketplace
cf create-service cleardb spark sql-mysql-cleardb
cf services

```

## MySQL Workbench 
**Project: cna-pas-springio-data-start**

Um auf die Datenbank zuzugreifen können Sie einen beliebigen Datenbank SQL Client verwenden. 
In der Schulung verwenden wir die **MySQL Workbench**. 

Um eine Datenbankverbindung mit der Workbench zu erstellen, benötigen Sie die Zugangsdaten der Datenbank.
Oeffenen Sie den **Application Manager**, waehlen Sie den erstellten Datenbank Service aus und rufen Sie die 
Management-Seite des Services auf, um die Zugangsdaten zu ermitteln. 

Erstellen Sie nun mit den Zugangsdaten über MySQL Workbench eine Verbindung zur Datenbank. 


## Spring Data Einführen  
**Project: cna-pas-springio-data-start**

Fuegen Sie den Route Service die folgende Dependency hinzu. 

```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

```

## Manifest 
**Project: cna-pas-springio-data-start**

Konfigurieren Sie den Route Service so, dass er den erstellten MySQL Cloud Service verwendet.

```
applications:
  - name: RouteService 
    random-route: true
    path: target/cna-pas-springio-service-final-0.0.1-SNAPSHOT.jar
    services:
    - sql-mysql-cleardb
    
```
 
## Build 
**Project: cna-pas-springio-data-start**

```
mvn clean package

```

## Deployment
**Project: cna-pas-springio-data-start**

 
```
cf push 

```

## Spring Cloud Task Dependency hinzufügen 
**Project: cna-pas-springio-data-migration-start**


``` 
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-task</artifactId>
</dependency>

```

## Enable Task Execution 
**Project: cna-pas-springio-data-migration-start** 

Aktivieren Sie in der Klasse Application den Task und schauen Sie sich das Life-Cycle Interface an. 


```java
@EnableTask
}  
```

## Flyway Dependency prüfen  
**Project: cna-pas-springio-data-migration-start**

Normalerweise ist unter Spring Boot 2.x die Flyway Version > 6.0 definiert. Allerdings unterstützt diese nicht die 
unter Pivotal Web Services bereitgestellte MySQL 5.5 die hinter ClearDB steht. MySQL Verionen > 5.5 können nur über 
die kostenpflichtige Enterprise Edition von Flyway verwendet werden.  


``` 
<dependency>
	<groupId>org.flywaydb</groupId>
	<artifactId>flyway-core</artifactId>
	<version>5.2.3</version>
</dependency>

```

## Properties check
**Project: cna-pas-springio-data-migration-start**

Die folgenden Properties sind in der **application.properties** schon definiert. 


``` 

flyway.locations=classpath:db/migration
flyway.enabled=true
flyway.checkLocation=false
```

## Migration Skript
**Project: cna-pas-springio-data-migration-start**

Unter dem Verzeichnis **resource/db/migration** finden Sie das Migrationsskript, dass genau eine Tabelle in der Datenbank anlegt und Daten hinzugfügt. 


## Manifest 
**Project: cna-pas-springio-data-migration-start**

Über das Manifest kann die Zuordnung einer Route **no-route: true** verhindert werden. Das Management wird über 
**health-check-type: process** ausgeschaltet. Diese Einstellungen sind notwendig, da die App ja nicht dauerhaft 
ausgeführt und vom Health-Management überwacht werden soll. 

```
---
applications:
  - name: routeServiceDataMigration 
    no-route: true
    path: target/cna-pas-springio-data-migration-start-0.0.1-SNAPSHOT.jar
    health-check-type: process
    services:
    - sql-mysql-cleardb
    
```

## Build 
**Project: cna-pas-springio-data-migration-start**

```
mvn clean package

```

## Deployment 
**Project: cna-pas-springio-data-migration-start**
 
```
cf push 

```

## Start Task 
**Project: cna-pas-springio-data-migration-start**

Starte Task über Route Service Migration App. 

```
cf run-task routeServiceDataMigration ".java-buildpack/open_jdk_jre/bin/java org.springframework.boot.loader.JarLauncher" --name routeServiceDataMigration-task


```

## Prüfen der Daten über SQL Client Workbench 
**Project: cna-pas-springio-data-migration-start**

Prüfen Sie ob die Tabelle in der Datenbank angelegt wurde.  




## Application Data 

**Project: cna-pas-springio-data-start**

Ueber  **Curl** kann ueberprueft werden, ob die Daten von der Anwendung bereitgestellt werden koennen. 

 ```
curl <Route>/routes  

```

