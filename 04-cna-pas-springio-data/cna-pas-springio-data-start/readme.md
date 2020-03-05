## Aufgabe  

 
1. Erstellen Sie einen MySQL Cloud Service 
2. Verbinden Sie einen SQL Client z.B. MySQL Workbench mit der Datenbank 
3. Binden Sie den Route Service an die Datenbank an 
4. Rufen Sie über das API alle Routen auf 

## Login 

```
cf login -a api.run.pivotal.io -u <user> -p <password> 

```

## MySQL Datenbank erstellen  

Suchen Sie den richtigen MySQL Cloud Service aus dem Marketplace aus und installieren Sie diesen. 
Achten Sie darauf, dass die Installation im hintergrund durchgeführt wird und ein weile dauern kann. 
Überprüfen Sie den Status der Installation. 


```
cf marketplace
cf create-service cleardb spark sql-mysql-cleardb
cf services

```

## MySQL Workbench 
Um auf die Datenbank zuzugreifen können Sie einen beliebigen Datenbank SQL Client verwenden. 
In der Schulung verwenden wir die **MySQL Workbench**. 

Um eine Datenbankverbindung mit der Workbench zu erstellen, benötigen Sie die Zugangsdaten der Datenbank.
Öffenen Sie den Application Manager, wählen Sie den erstellten Datenbank Service aus und rufen Sie die 
Management-Seite des Services auf, um die Zugangsdaten zu ermitteln. 

Erstellen Sie nun mit den Zugangsdaten über MySQL Workbench eine Verbindung zur Datenbank. 


## Introduce Spring Data 
Fügen Sie den Route Service die folgende Dependency hinzu. 

```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

```


## Manifest 

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

```
mvn clean package

```

## Deployment

**ACHTUNG** Bevor Sie nun das Deployment durchführen können, benötigen Sie eine Datenbank mit den notwendigen Tabellen etc. 
Diese Datenbank wird nun über einen Cloud Task angelegt. Wechseln Sie dazu zum Projekt **cna-pas-springio-data-migration-start**. 

 
```
cf push 

```

## Application Data 

Über  **Curl** kann überprüft werden, ob die Daten über die Anwendung bereitgestellt werden können. Es sollte nun eine Leere Liste 
angezeigt werden. 

 ```
curl <Route>/routes  

```

**ACHTUNG** Wechseln Sie nun zum Projekt **cna-pas-springio-data-migration-start** und füllen Sie die Datenbank mit ein paar Daten. 


Über **Curl** kann überprüft werden, ob die Daten über die Anwendung bereitgestellt werden können. 

