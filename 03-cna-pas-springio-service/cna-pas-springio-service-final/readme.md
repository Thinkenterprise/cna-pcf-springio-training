## Task 


1. Create App Autoscaler Service to scale our application 
2. Deploy your App and bind it to the App Autoscaler service over your manifest
3. Configure the App Autoscaller for HTTP latency between 10ms 20ms
3. Benschmark your App with Apache Bench 



## Login 

```
cf login -a api.run.pivotal.io -u <user> -p <password> 

```


## App Autoscaler CLI 

Install the App Autoscaler CLI to work with App Autoscaler over command line 


```
cf install-plugin ./autoscaler-for-pcf-cliplugin-macosx64-binary-2.0.233

```


## Install App Autoscaler Service 


```
cf create-service app-autoscaler standard autoscaler

```

## Build 

```
mvn clean package

```

## Manifest 

Bind App Autoscaler Service to your App over the manifest.yml 

```
applications:
  - name: RouteService 
    random-route: true
    path: target/cna-pas-springio-service-final-0.0.1-SNAPSHOT.jar
    services:
    - autoscaler
    
```

## Deployment 
```
cf push 

```



## Configure Service 


Set the min and max instance. 

```
cf update-autoscaling-limits routeService 1 3

```

Set the scalling rule for our app with rule type HTTP latency between 10 and 20 ms 


```
cf create-autoscaling-rule routeService http_latency 10 20 -s avg_99th

```

Enable autoscaling 

```
cf enable-autoscaling test-app-2

```


Controle the autoscaling apps and rules 


```
cf autoscaling-apps

```


## Apache Bench 


To stress our application we use the Apache Benchmark tool an call 

```
ab -n 10000000 -c 10 -t 60 <URL>/traffic


```
 

