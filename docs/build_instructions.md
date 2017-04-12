#Dev Setup and Usage

## Database Setup
`./gradlew flywayclean`
`./gradlew flywaymigrate`

## UI 
#### Compile
`npm install`
`webpack`

#### Debug
`npm run dev`
This launches a nodejs express server and runs webpack.  Code is auto-updated

open http://localhost:7777

## Server 
#### Compile
`./gradlew clean`   
`./gradlew build`

#### Debug
`./gradlew eclipse`
Open eclipse. Build Project. Run Project

## Execute
#### Execute Back-End the Command Line
`./run.sh`
Open http://localhost:8080
