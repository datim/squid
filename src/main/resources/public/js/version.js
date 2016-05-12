"use strict";

//
// return the results in version
//
function getVersion() {

  // make the call
  var xhr = new XMLHttpRequest();
  var url = "http://localhost:8080/crawl/version";
  xhr.open("GET", url, false);
  xhr.send();

  var version = "<p align=right> Squid " + xhr.responseText + "</p>";

  document.write(version);
}

//
// start a search for nodes and display results
//
function startSearch() {

  var xhr = new XMLHttpRequest();
  var url = "http://localhost:8080/crawl/go";
  xhr.open("GET", url, false);
  xhr.send();
}

//
// Add the search results to an existing table
//
function reportResults() {
  var xhr = new XMLHttpRequest();
  xhr.open("GET", "http://localhost:8080/crawl/photos", false);
  xhr.send();

  var photoResults = JSON.parse(xhr.responseText);

  var table = document.getElementById("PhotoResultsTable");

  // write a table of photos
  var photosPerRow = 2;
  var i = 0;
  var row = table.insertRow();
  for (i = 0; i < photoResults.length; i++) {

    // create a new row after all the photos have been exhausted
    if (i % photosPerRow == 0) {
      row = table.insertRow();
    }

    insertTableRowCell(row, photoResults[i].url);
  }
}

// insert a photo into a table row cell
function insertTableRowCell(row, photoURL) {
  var cell = row.insertCell();
  var image = document.createElement('img');
  image.src = photoURL;
  cell.appendChild(image);
}
