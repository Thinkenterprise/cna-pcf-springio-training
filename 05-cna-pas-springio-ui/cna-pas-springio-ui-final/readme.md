## Task 



1. Installieren Sie den Route Service und rufen das UI auf 
2. Installieren Sie den Route Service und rufen die Open API Specification auf 
3. Installieren Sie den Route Service und rufen die API Dokumentation auf 
4. Fügen Sie der UI ein Feld Flüge **flight** hinzu und installieren Sie die Anwendung neu 
5. Fügen Sie dem API eine Operation zum löschen einer Route hinzu und installieren Sie die Anwendung erneut   

1. Fügen Sie 



## Login 

```
cf login -a api.run.pivotal.io -u <user> -p <password> 

```


## Build 

```
mvn clean package

```

## Deployment 
```
cf push 

```


## UI 


Die UI rufen Sie auf indem Sie die URL des Service ermitteln und dann über einen **Browser** eingeben. 

 
```
cf apps
Browser : http://<Service-url> 

```
## API  

Die **Open API Specification** rufen Sie auf indem Sie die URL des Service ermitteln und dann über **Postma** ``v2/api-docs `` eingeben. 


```
cf apps
Postman : http://<Service-url>/v2/api-docs

```

 

Die **Open API Documentation** rufen Sie auf indem Sie die URL des Service ermitteln und dann über **Postma** ``swagger-ui.html `` eingeben. 


```
cf apps
Postman : http://<Service-url>/swagger-ui.html

```

## UI Changes 
Für die Änderungen an der UI müssen Sie die Seite index.html und add-route.html anpassen. 
Damit die Daten auch bereitgestellt werden könne, ist eine Anpassung der ``Route`` und des Initialisierungsskripts ``data-mysql.sql`` notwendig.  


Änderungen an der Entity Route.

```java
@Entity
public class Route  {
	
	private Integer flights;
	
```

Änderungen an dem Skript.

```java
insert into route(id, flight_Number, departure, destination, flights) values(101, 'LH7902','MUC','IAH',0);
```


## API Changes 
Die Änderungen am API müssen am API Controller `RouteController` durchgeführt werden. 

```java
@ApiOperation(value="Delete one route", notes="Delete one route from the repository", nickname="getAll", response=Void.class)
	@ApiResponses(value= {@ApiResponse(code=204, message="no content"), @ApiResponse(code=400, message="can't access routes ",response=Error.class) })
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@ApiParam(name="id", value="route identifier", required=true, type="Long") @PathVariable(value = "id") Long id) {
		routeRepository.deleteById(id);
	}
```

