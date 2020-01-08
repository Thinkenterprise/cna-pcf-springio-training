
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
-p cf push helloWorldService -p cna-pcf-springio-apps-final-0.0.1-SNAPSHOT.jar

```

## Manifest 

Create Manifest File manifest.yml 

```
applications:
  - name: helloWorldService 
    random-route: true
    path: target/cna-pcf-springio-apps-final-0.0.1-SNAPSHOT.jar
```

The route oft application will be created by the name of the application.
In case of more than one developer work on the same organisation it gives conflicts.  Then the manifest variable ```random-route ``` should solve  the problem. 


## Buildpack 

Add a Buildpack to the manifest. The sample show three different way to define the manifest by **name** by **repository-URL** and by **repository URL with version**

```
applications:
  - name: helloWorldService 
  ...
    buildpacks: 
      - java-buildpack
      - https://github.com/cloudfoundry/java-buildpack.git
      - https://github.com/cloudfoundry/java-buildpack.git#v4.27
```

A list of the existing buildpacks 

How to build your own buildpacks is described in the documentation. 

A creation of a custom buildpack in Pivotal Web Services is not possible.

```
cf create-buildpack BUILDPACK PATH POSITION [--enable|--disable]
```

## Environment 

Add a two environment variables ROUTE_SERVICE_ENVIRONMENT an SPRING_PROFILES_ACTIVE to the application 


```
applications:
  - name: helloWorldService 
  ...
    env:
      ROUTE_SERVICE_ENVIRONMENT: production
      SPRING_PROFILES_ACTIVE: production
```


Use the environment variable in the implementation 

```
@Value("${route.service.environment}"
private String applicationEnvironment;


```


## Scaling 

The manifest variable ``instance``, ``memory`` and ``disk_quota`` are n to scale the application honizontal. 

```
applications:
  - name: helloWorldService 
  ...
    memory: 2G 
    disk_quota: 512M
    instances: 2
```

## Health Check  

```
applications:
  - name: helloWorldService 
  ...
    health-check-type: http
    health-check-http-endpoint: /actuator/health
```

## Docker 


 

