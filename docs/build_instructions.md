# Development Usage
Compiling and debugging project.  Front End and Back End can 
be developed seperately

## Setup Enviornment
Before building, dependent libraries must be installed

#### Front End Env
Install packages using npm
`npm install`

Packages will be installed locally at ./node_modules

#### Back End Env
Install Eclipse environment
`./gradlew eclipse`

Download packages through Eclipse
`./gradlew clean build`

## Build/Run Front End

#### Build Front End
`webpack --config webpack.config.js` or `npm run build`

#### Launch in Debug Mode From Command Line
`npm run dev`
This launches a nodejs express server using webpack-run-dev and runs webpack.  Code is hot-swapped on the fly.

Open URL `http://localhost:7777`

* Make sure there is a backend server running. Either run the entire program, or start the project through eclipse.
* Debug in chrome.

## Build/Run Back End 

#### Build Back End
`./gradlew clean build`   

#### Launch in Debug Mode Through Eclipse
`./gradlew eclipse`
Run project through debug mode in eclipse with following settings:

```
VM Variables:
-DROOT=C:/users/datim/Desktop/squid -DLOG_LEVEL=info
```

To run with an in memory database, add the following program arguments:
`--spring.config.name=debug.properties`

## Automated Build+Launch Scripts

#### Compile And Launch in 'Production' Mode
`./build.sh`

Open URL `http://localhost:8080`

#### Compile And Launch in 'Debug' Mode
`./build_dev.sh`

Open URL `http://localhost:8080`

## Migrate Database
In order to migrate the database manually, use flyway commands:

`./gradlew flywayclean`
`./gradlew flywaymigrate`

