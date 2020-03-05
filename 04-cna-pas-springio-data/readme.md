## Aufgabe  

Die Fluggesellschaft m�chte den Route Service nun an eine MySQL Datenbank anbinden. 
Die Datenbankmigration soll �ber ein Task App, basierend auf Flyway und Spring Cloud Task, 
durchgef�hrt werden. Die Task App soll �ber einen Cloud Task aufgerufen werden. 


1. MySQL Cloud Service erstellen 
2. Task App f�r Datenmigration erstellen 
3. Cloud Task auf Task App ausf�hren 



## Login 
**Project: cna-pas-springio-data-start**


```
cf login -a api.run.pivotal.io -u <user> -p <password> 

```

## MySQL Datenbank erstellen  
**Project:  cna-pas-springio-data-start**


Suchen Sie den richtigen MySQL Cloud Service aus dem Marketplace aus und installieren Sie diesen. 
Achten Sie darauf, dass die Installation im hintergrund durchgef�hrt wird und ein weile dauern kann. 
Überprüfen Sie den Status der Installation. 


```
cf marketplace
cf create-service cleardb spark sql-mysql-cleardb
cf services

```

## MySQL Workbench 
**Project: cna-pas-springio-data-start**

Um auf die Datenbank zuzugreifen k�nnen Sie einen beliebigen Datenbank SQL Client verwenden. 
In der Schulung verwenden wir die **MySQL Workbench**. 

Um eine Datenbankverbindung mit der Workbench zu erstellen, ben�tigen Sie die Zugangsdaten der Datenbank.
Öffenen Sie den Application Manager, wählen Sie den erstellten Datenbank Service aus und rufen Sie die 
Management-Seite des Services auf, um die Zugangsdaten zu ermitteln. 

Erstellen Sie nun mit den Zugangsdaten �ber MySQL Workbench eine Verbindung zur Datenbank. 


## Spring Data Einf�hren  
**Project: cna-pas-springio-data-start**

Fügen Sie den Route Service die folgende Dependency hinzu. 

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
## Spring Cloud Task Dependency hinzuf�gen 
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

## Flyway Dependency hinzuf�gen 
**Project: cna-pas-springio-data-migration-start**

Normalerweise ist unter Spring Boot 2.x die Flyway Version > 6.0 definiert. Allerdings unterst�tzt diese nicht die 
unter Pivotal Web Services bereitgestellte MySQL 5.5 die hinter ClearDB steht. MySQL Verionen > 5.5 k�nnen nur �ber 
die kostenpflichtige Enterprise Edition von Flyway verwendet werden.  


``` 
<dependency>
	<groupId>org.flywaydb</groupId>
	<artifactId>flyway-core</artifactId>
	<version>5.2.3</version>
</dependency>

```

## Properties 
**Project: cna-pas-springio-data-migration-start**

Die folgenden Properties sind in der application.properties schon definiert. 


``` 

flyway.locations=classpath:db/migration
flyway.enabled=true
flyway.checkLocation=false
```

## Migration Skript
**Project: cna-pas-springio-data-migration-start**

Unter dem Verzeichnis **resource/db/migration** finden Sie das Migrationsskript, dass genau eine Tabelle in der Datenbank anlegt. 


## Manifest 
**Project: cna-pas-springio-data-migration-start**

�ber das Manifest kann die Zuordnung einer Route **no-route: true** verhindert werden. Das Management wird �ber 
**health-check-type: process** ausgeschaltet. Diese Einstellungen sind notwendig, da die App ja nicht dauerhaft 
ausgef�hrt und vom Health-Management �berwachst werden soll. 

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

Starte Task �ber Route Service Migration App. 

```
cf run-task routeServiceDataMigration ".java-buildpack/open_jdk_jre/bin/java org.springframework.boot.loader.JarLauncher" --name routeServiceDataMigration-task


```

## Pr�fen der Daten �ber SQL Client Workbench 
**Project: cna-pas-springio-data-migration-start**

Pr�fen Sie ob die Tabelle in der Datenbank angelegt wurde.  




## Application Data 

**Project: cna-pas-springio-data-start**

Über  **Curl** kann überprüft werden, ob die Daten über die Anwendung bereitgestellt werden können. Es sollte nun eine Leere Liste 
angezeigt werden. 

 ```
curl <Route>/routes  

```

## Add Migration Script 
**Project: cna-pas-springio-data-migration-start**

F�gen Sie ein neues Migrationsskript hinzu **V2__insert_routes.sql** und f�gen Sie dort folgende Zeilen ein. 

``` 
insert into route(id, flight_number, departure, destination) values(101, 'LH7902','MUC','IAH');
insert into route(id, flight_number, departure, destination) values(102, 'LH1602','MUC','IBZ');
insert into route(id, flight_number, departure, destination) values(103, 'LH401','FRA','NYC');
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

Starte Task �ber Route Service Migration App. 

```
cf run-task routeServiceDataMigration ".java-buildpack/open_jdk_jre/bin/java org.springframework.boot.loader.JarLauncher" --name routeServiceDataMigration-task


```


## Application Data 

**Project: cna-pas-springio-data-start**

Über  **Curl** kann überprüft werden, ob die Daten über die Anwendung bereitgestellt werden können. Es sollten nun drei Datens�tze angezeigt werden.

 ```
curl <Route>/routes  

```
