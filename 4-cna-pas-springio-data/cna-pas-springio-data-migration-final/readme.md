## Task 

1. Erstelle eine Task Migration App mit Spring Cloud Task. Gebe den Life-Cycle des Task über die Console aus 
2. Die Task Migration App soll bei der Ausführung eine Datenbank Migration über Flyway durchführen über die 
   drei Datensätze in die Route Tabelle geladen werden. 
3. Die Task Migration App soll als App installiert und über einen PAS-Task gestartet werden 
4. Über die Data Workbensch soll die Datenbankmigration nachvollzogen werden.  


## Add Spring Cloud Task Dependency 

``` 
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-task</artifactId>
</dependency>

```

## Task Life-Cycle Interface 

```java
@SpringBootApplication
@EnableTask
public class Application implements ApplicationRunner, TaskExecutionListener {
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		logger.info("Migrate Database ...");
	}

	@Override
	public void onTaskStartup(TaskExecution taskExecution) {
		logger.info("Spring Cloud Task Migrate Database ... Startup");
	}
		
	

	@Override
	public void onTaskEnd(TaskExecution taskExecution) {
		logger.info("Spring Cloud Task Migrate Database ... End ");
		
	}

	@Override
	public void onTaskFailed(TaskExecution taskExecution, Throwable throwable) {
		logger.info("Spring Cloud Task Migrate Database ... Failed ");
		
	}
	
}
    
```

## Add Flyway Dependency 

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


## Add Migration Script 

Zuvor ist ein entsprechendes Migration-Script mit dem Namen V2__... anzulegen. In dieser Datei sind dann folgende Einträge abzulegen. 

``` 
insert into route(id, flightNumber, departure, destination) values(101, 'LH7902','MUC','IAH');
insert into route(id, flightNumber, departure, destination) values(102, 'LH1602','MUC','IBZ');
insert into route(id, flightNumber, departure, destination) values(103, 'LH401','FRA','NYC');
```



## Manifest 

Über das Manifest kann dieZuordnung einer Route **no-route: true** verhindert werden. Das Management wird über 
**health-check-type: process** ausgeschaltet. Fixme: Die App wird trotzdem regelmässig gestartet. 

```
---
applications:
  - name: routeServiceDataMigration 
    no-route: true
    path: target/cna-pas-springio-data-migration-final-0.0.1-SNAPSHOT.jar
    health-check-type: process
    services:
    - sql-mysql-cleardb
    
```

## Build 

```
mvn clean package

```


## Login 

```
cf login -a api.run.pivotal.io -u <user> -p <password> 

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

## Verify Data over Data Workbench 

Die Verbindungsdaten der Datenbank können über die Oberfläche von Pivotal Web Services bestimmt werden oder über das Enviroment der App selbst. 

 ```
cf env routeServiceDataMigration 

```

Starten Sie die Workbench **Sequel Pro** um die Daten zu prüfen. 















