## Aufgabe  

Die Fluggesellschaft möchte einen Route Service bereitstellen der in Spring Boot implementiert ist. 


1. Fügen Sie einen Web Starter in die POM 
2. Erstellen Sie einen Rest-API über das der Text **Hello World** zurückgegeben wird 


## Starter 

Fügen Sie den folgenden Starter in die POM 

```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-web</artifactId>
</dependency>

```


## Spring Boot Application  

Erstellen Sie eine Spring Boot Application 

```
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```

## Hello World Controller  

Erstellen Sie eine Klasse RouteController, machen Sie daraus einen Rest Controller @RestController und stellen Sie ein Rest API 
/helloWorld über  @RequestMapping("/helloWorld") bereit das Hello World zurückgibt. 


```
@RestController
public class RouteController {

	 @RequestMapping("/helloWorld")
	 public ResponseEntity<String> index() {
	     return ResponseEntity.ok("Hello World");
	 }
} 

```

## Build 
```
mvn clean package

```

## Run  
```
java -jar cna-pas-springio-final-0.0.1-SNAPSHOT.jar

```

## Test  

```
curl localhost:8080/helloWorld

```

