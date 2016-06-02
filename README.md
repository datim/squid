#Dev Setup and Usage

### Database Setup
`./gradlew flywayclean`
`./gradlew flywaymigrate`

### Front-End Compile
(optional) `npm install webpack -g`

`npm install`
`webpack`

### Server Compile
Install all npm based packages:

`./gradlew clean`   
`./gradlew build`

### Execute Back-End the Command Line
`./run.sh`

### Execute Front-End Dev Setup
`npm run dev`

This launches a nodejs express server and runs webpack.  Code is auto-updated.
Access frontend test server through http://localhost:7777

Optionally, launch the backend server to test REST APIs with front-end

### Auto-load front-end code
`webpack --watch`

### Execute Back-End from Eclipse
'./gradlew eclipse'

Open eclipse. Build Project. Run Project

# References
ReactJS/Webpack - https://www.youtube.com/watch?v=MhkGQAoc7bc&list=PLoYCgNOIyGABj2GQSlDRjgvXtqfDxKm5b
