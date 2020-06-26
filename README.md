# cp4i-financial-websocket-ms project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## What this microservice does
Currently this microservice acts as the last of the financial scenario microservices. It will pull from the Kafka topic and deserializes the JSON into a Java object. The service will then validate if all booleans are false. If so it commits it to a final channel to be used with the WebSocket service.


## Setting the environment variables prior to use

Run the following command and replace the items in <> with your values prior to running the application. Note that the CERT_LOCATION environment variable is only necessary if you need to connect to an Event Streams instance on Cloud Pak for Integration for example.

```shell
export BOOTSTRAP_SERVERS=<your-bootstrap-server> \ 
export TOPIC_NAME=<your-topic-name> \
export API_KEY=<your-api-key> \
export CERT_LOCATION=<path-to-truststorefile/es-cert.jks>
```


## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
./mvnw quarkus:dev
```

Note - if you plan to spin up the other Financial project microservices locally you will need to add a -Ddebug=<port> to the above run command as the default puts the local service at port 5005.

## Packaging and running the application

The application can be packaged using `./mvnw package`.
It produces the `cp4i-financial-websocket-ms-1.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/cp4i-financial-websocket-ms-1.0-SNAPSHOT-runner.jar`.

## Testing and Running a JVM Docker Image 

Note - For usage with Event Streams on the IBM Cloud Pak for Integration, copy your .jks certificate file into the src/main/resources/ssl folder prior to the docker build.

Build the package if you haven't already

```shell
mvn package
```

Next we can build the JVM Docker image

```shell
docker build -f src/main/docker/Dockerfile.jvm -t quarkus/cp4i-financial-websocket-ms-jvm .
```

Finally to run the newly built Docker image we need to provide a few environment variables (that we exported locally earlier) since the values are not hard coded into our application.properties file.

```shell
docker run -i --rm -p 8085:8085 --env BOOTSTRAP_SERVERS --env TOPIC_NAME --env API_KEY --env CERT_LOCATION=/work/ssl quarkus/cp4i-financial-websocket-ms-jvm
```

You may notice that the CERT_LOCATION environment variable here is overwritten in this Docker run command and that's due to the docker image needing the filepath relative to the location inside of the container and not in the local environment.

## Creating a native executable

You can create a native executable using: `./mvnw package -Pnative`.

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: `./mvnw package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your native executable with: `./target/cp4i-financial-websocket-ms-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image.



## (TO-DO) Kubernetes and OpenShift Deployment, Secrets and ConfigMaps
