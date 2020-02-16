## Task 


1. Stellen Sie dem Route Service einen Zugriff auf eine MySQL Datenbank zur Verfügung 
2. Erstellen Sie einen MySQL Cloud Service 
3. Konfigurieren Sie die den Route Service so, dass der MySQL Cloud Service verwendet wird 
3. Installieren Sie den Route Service in der PAS 
4. Rufen Sie über das API die Daten ab.  


## Introduce Spring Data 

```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

```


## Login 

```
cf login -a api.run.pivotal.io -u <user> -p <password> 

```


## MySQL Datenbank erstellen  

Suchen Sie den richtigen Cloud Service aus dem Marketplace aus und installieren ihn. 
Achten Sie darauf, dass die Installation im hintergrund durchgeführt wird und ein weile dauern kann. 
Überprüfen Sie den Status der Installation. 


```
cf marketplace
cf create-service cleardb spark sql-mysql-cleardb
cf services

```

## Manifest 

Route Service so konfigurieren, dass er den erstellten MySQL Cloud Service verwendet.

```
applications:
  - name: RouteService 
    random-route: true
    path: target/cna-pas-springio-service-final-0.0.1-SNAPSHOT.jar
    services:
    - autoscaler
    - sql-mysql-cleardb
    
```
 

## Build 

```
mvn clean package

```

## Deployment 
```
cf push 

```

## Verify Data over Data Workbench 

Die Verbindungsdaten der Datenbank können über die Oberfläche von Pivotal Web Services bestimmt werden oder über das Enviroment der App selbst. 

 ```
cf env routeServiceDataMigration 

```

Starten Sie die Workbench **Sequel Pro** um die Daten zu prüfen. 


## Application Data 

Über die Command Line und **Curl** kann überprüft werden, ob die Daten über die Anwendung bereitgestellt werden können.  

 ```
curl <Route>/routes  

```







