# Development Usage
Compiling and debugging project.  Front End and Back End can 
be developed seperately

## Setup Enviornment
Before building, dependent libraries must be installed

#### Front End Env
Install packages using npm. Install webpack globally, and all other packages locally in **./node_modules** directory.
`npm install -g webpack`
`npm install`

#### Back End Env
Install Eclipse environment
`./gradlew eclipse`

Download packages through Eclipse
`./gradlew clean build`

## Helper Scripts
There are two helper scripts that will perform compiling and running for you.

Compile in either regular or debug mode:
`./build.sh [debug]`

Run in either regular or debug mode:
`./run.sh [debug]`

## Build Details
To build components by hand, follow these instructions

### Build UI Code
Package the UI code into a web pack for loading into Java application
`webpack --config webpack.config.js`

Package the UI code into a web pack for loading into Java application with debug
`webpack --config webpack.config.dev.js`

### Build Back End Code
Simply run gradle. There are no debug options
`./gradlew clean build`   


## Run Details

### Run UI Code in Dev Mode
This command launches a nodejs express server using webpack-run-dev and runs webpack.  Code is hot-swapped on the fly.
`npm run dev`

Open URL `http://localhost:7777`

* Make sure there is a backend server running. Either run the entire program, or start the project through eclipse.
* Debug in chrome.

### Launch Application in Debug mode through eclipse
`./gradlew eclipse`
Run project through debug mode in eclipse with following settings:

```
VM Variables:
-DROOT=C:/users/datim/Desktop/squid -DLOG_LEVEL=info
```

To run with an in memory database, add the following program arguments:
`--spring.config.name=debug.properties`

### Run Application through command line 
Normal Mode:
`java -DROOT=[ROOT DIRECTORY for LOG files] -DLOG_LEVEL=info -jar build/libs/squid-1.0.jar`

Debug Mode:
`java -DROOT=[ROOT DIRECTORY for LOG files] -DLOG_LEVEL=info -jar build/libs/squid-1.0.jar --spring.config.name=debug.properties`

Open URL `http://localhost:8080`


## Migrate Database
In order to migrate the database manually, use flyway commands:

`./gradlew flywayclean`
`./gradlew flywaymigrate`

