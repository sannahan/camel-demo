# Demo project for Apache Camel on Quarkus

Your task is to fix the Camel route in the CoalConsumptionRoute class. 
Currently, the route returns an error, so the data is read from the database.
The data in the database is imaginary data painting a very rosy picture of decreasing coal usage.
Fix the Camel route to get accurate data from the Statistics Finland database.
Find examples of imaginary and accurate coal usage stats below. When http://localhost:8080/measurements returns the accurate stats (column on the  right), you have solved the task.

| Year | Imaginary coal usage stats (1000 t) | Accurate coal usage stats (1000 t) |
|------|-------------------------------------|------------------------------------|
| 1970 | 5000                                | 2824                               |
| 2022 | 10                                  | 2014                               |

<details>
<summary>Answer</summary>
<br>

    from(DIRECT_GET_COAL_CONSUMPTION)
    .log("Fetching coal consumption in Finland between 1970-2022")
    .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.POST))
    .setHeader(HttpHeaders.CONTENT_TYPE, constant(ContentType.APPLICATION_JSON))
    .bean(this, "getQuery")
    .to("https://pxdata.stat.fi:443/PxWeb/api/v1/en/StatFin/kivih/statfin_kivih_pxt_11l7.px")
    .setProperty("values", jsonpath("$.value"))
    .log("Values: ${exchangeProperty.values}")
    .bean(this, "mapData");
</details>

## Setup

Camel Extensions for Quarkus require Java 17 as the minimum JDK.

Update the datasource path in application.properties to match your system.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/demo-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.


