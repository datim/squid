# Development Guide
Compiling and debugging project.  Client and server can be built seperately.

## Client

#### Enviornment Setup
Prior to building, setup client environment. Install webpack globally, 
and all other packages locally in **./node_modules** directory.

```
# npm install -g webpack@2.3.3
# npm install
```

#### Build
The client must be packaged using webpack prior to loading into the Java application:

Production:
`webpack --config webpack.config.js`

Development (includes debug):
`webpack --config webpack.config.dev.js`

or alternatively:
`./build.sh [debug]`

#### Execute
**Dev Mode**
Launches nodejs express server using webpack-run-dev.  Code is hot-swapped on the fly. 
`npm run dev`

and connect to URL:
`http://localhost:7777`

To test full functionality, start a server instance.

## Server

#### Environment Setup
Prior to building, setup server environment:
`./gradlew eclipse`

#### Build
`./gradlew clean build`

or alternatively:
`./build.sh [debug]`

#### Execute
**Eclipse**:
`./gradlew eclipse`
Run project through debug mode in eclipse with following settings:

```
VM Variables:
-DROOT=C:/users/datim/Desktop/squid -DLOG_LEVEL=info
```

To run with an in memory database, add the following program arguments:
`--spring.config.name=debug.properties`

**CLI**

Normal Mode:
`java -DROOT=[ROOT DIRECTORY for LOG files] -DLOG_LEVEL=info -jar build/libs/squid-1.0.jar`

Debug Mode:
`java -DROOT=[ROOT DIRECTORY for LOG files] -DLOG_LEVEL=info -jar build/libs/squid-1.0.jar --spring.config.name=debug.properties`

Open URL `http://localhost:8080`

## Client and Server
#### Build
To build both the client and server together in production or debug mode:
`./build.sh [debug]`

#### Execute
To run both the client and server together in production or debug mode:
`./run.sh [debug]`

## Migrate Database
In order to migrate the database manually, use flyway commands:

`./gradlew flywayclean`
`./gradlew flywaymigrate`

