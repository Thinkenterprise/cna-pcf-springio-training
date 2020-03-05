## Task 

1. Erstelle eine Task Route Service Migration App mit Spring Cloud Task Framework. Gebe den Life-Cycle des Task über die Console aus 
2. Die Task Migration App soll bei der Ausführung eine Datenbank Migration über Flyway durchführen über die 
   drei Datensätze in die Route Tabelle geladen werden. 
3. Die Task Migration App soll als App installiert und über einen PAS-Task gestartet werden 
4. Über die Data Workbensch soll die Datenbankmigration nachvollzogen werden.  




**ACHTUNG** : Wechseln Sie nun zum Projekt **cna-pas-springio-data-final** und Deployen Sie den Route Service. 




## Deployment

**ACHTUNG** Bevor Sie nun das Deployment durchführen können, benötigen Sie eine Datenbank mit den notwendigen Tabellen etc. 
Diese Datenbank wird nun über einen Cloud Task angelegt. Wechseln Sie dazu zum Projekt **cna-pas-springio-data-migration-start**. 


 
```
cf push 

```


## Add Migration Script 
Fügen Sie ein neues Migrationsskript hinzu **V2__insert_routes.sql** und fügen Sie dort folgende Zeilen ein. 

``` 
insert into route(id, flight_number, departure, destination) values(101, 'LH7902','MUC','IAH');
insert into route(id, flight_number, departure, destination) values(102, 'LH1602','MUC','IBZ');
insert into route(id, flight_number, departure, destination) values(103, 'LH401','FRA','NYC');
```


## Build 

``` 
mvn clean package

```


## Deployment 
 
```
cf push 

```

## Start Task 
Start des Migration App Task. 

```
cf run-task routeServiceDataMigration "Task" --name TASK-NAME

```









```



















