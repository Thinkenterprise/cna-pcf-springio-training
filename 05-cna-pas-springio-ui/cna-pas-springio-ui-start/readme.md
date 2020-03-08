## Aufgabe  



1. Fügen Sie dem Route Service ein **User Interface** hinzu, dass alle Routen im Browser anzeigt. 
Verwenden Sie dazu eine **Thin-Client** Technologie baiserend auf einer **HTML-Rendering Engine Thymeleaf**. 
2. Fügen Sie dem Route Service ein **User Interface** hinzu, dass über ein **REST API** alle Routen als JSON-Dokument 
zurückgibt. 


## Vorbereitung  
**Project: cna-pas-springio-ui-start**

Lösen Sie bitte alle bereits laufenden Apps und Datenbanktabellen.


## Login 
**Project: cna-pas-springio-ui-start**


```
cf login -a api.run.pivotal.io -u <user> -p <password> 

```

## Build 
**Project: cna-pas-springio-ui-start**

```
mvn clean package

```

## Deployment 
**Project: cna-pas-springio-ui-start**

```
cf push 

```


## UI - Webpage 
**Project: cna-pas-springio-ui-start**

Die **WebPage** rufen Sie auf, indem Sie die URL des Route Service ermitteln und dann über einen **Browser** eingeben. 

 
```
cf apps
Browser : http://<Service-url> 

```
## UI - REST API  
**Project: cna-pas-springio-ui-start**

Die **Open API Specification** rufen Sie auf indem Sie die URL des Service ermitteln und dann über **Postman** ``v2/api-docs `` eingeben. 


```
cf apps
Postman : http://<Service-url>/v2/api-docs

```

 

Die **Open API Documentation** rufen Sie auf indem Sie die URL des Route Service ermitteln und dann über einen **Browser** eingeben ``swagger-ui.html `` eingeben. 


```
cf apps
http://<Service-url>/swagger-ui.html

```

## UI Changes 
**Project: cna-pas-springio-ui-start**

Für die Änderungen an der WebPage müssen Sie die Seite **index.html** anpassen. 
Damit die Daten auch bereitgestellt werden könne, ist eine Anpassung der **Route** notwendig.  


Änderungen an der Entity Route.

```java
@Entity
public class Route  {
	
	@Transient
	private Integer flights;
	
```



In der **index.html** ist folgende Zeile an der richtigen Stelle zu ergänzen.
 
```html
<td th:text="${route.flights}"></td>
```

## API Changes 
**Project: cna-pas-springio-ui-start**

Fügen Sie dem Controller eine REST API Methode hinzu, mit dem Routen gelöscht werden können. 
Die Änderungen am API müssen am API Controller **RouteController** durchgeführt werden. 

```java
@ApiOperation(value="Delete one route", notes="Delete one route from the repository", nickname="getAll", response=Void.class)
	@ApiResponses(value= {@ApiResponse(code=204, message="no content"), @ApiResponse(code=400, message="can't access routes ",response=Error.class) })
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@ApiParam(name="id", value="route identifier", required=true, type="Long") @PathVariable(value = "id") Long id) {
		routeRepository.deleteById(id);
	}
```



