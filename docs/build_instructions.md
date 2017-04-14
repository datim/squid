#Dev Usage
Compiling and debugging project

## Frontend

#### Build
`webpack --config webpack.config.js` or `npm run build`

#### Debug
`npm run dev`
This launches a nodejs express server using webpack-run-dev and runs webpack.  Code is hot-swapped on the fly.

Open url 'http://localhost:7777'

* Make sure there is a backend server running. Either run the entire program, or start the project through eclipse.
* Debug in chrome.

## Backend 

#### Compile
`./gradlew clean`   
`./gradlew build`

#### Debug
`./gradlew eclipse`
Run project through debug mode in eclipse

## Execute Project
#### Execute Back-End the Command Line
`./run.sh`
Open http://localhost:8080

## Migrate Database
`./gradlew flywayclean`
`./gradlew flywaymigrate`

