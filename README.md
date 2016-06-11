#Dev Setup and Usage

## Database Setup
`./gradlew flywayclean`
`./gradlew flywaymigrate`

## Compile 
#### Front-End Compile
(optional) `npm install webpack -g`

`npm install`
`webpack`

#### Server Compile
Install all npm based packages:

`./gradlew clean`   
`./gradlew build`

## Execute
#### Execute Back-End the Command Line
`./run.sh`


## Development

### Execute Back-End from Eclipse
'./gradlew eclipse'

Open eclipse. Build Project. Run Project

#### Execute Front-End Dev Setup
`npm run dev`

This launches a nodejs express server and runs webpack.  Code is auto-updated.
Access frontend test server through http://localhost:7777

Optionally, launch the backend server to test REST APIs with front-end
