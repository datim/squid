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

/*
  var photoDiv = document.getElementById("PhotoResultsDiv");

  // add new sub-divisions containing photos
  var i = 0;
  for (i = 0; i < photoResults.length; i++) {

      var image = document.createElement('img')
      image.src = photoResults[i].url;
      image.width = 100;
      image.height = 100;

      var thumbDiv = document.createElement('div');
      thumbDiv.className = "nailthumb-container square"
      thumbDiv.appendChild(image);

      photoDiv.appendChild(thumbDiv);
  }
  */

  var table = document.getElementById("PhotoResultsTable");

  // write a table of photos
  var photosPerRow = 4;
  var row = table.insertRow();
  var i = 0;
  for (i = 0; i < photoResults.length; i++) {

    // create a new row after all the photos have been exhausted
    if (i % photosPerRow == 0) {
      row = table.insertRow();
    }

    insertTableRowCell(row, photoResults[i]);
  }
}

// insert a photo into a table row cell
function insertTableRowCell(row, photoResult) {

  var image = document.createElement('img');
  image.src = photoResult.url;
  image.width = 200;
  image.height = 200;

  var thumbDiv = document.createElement('PhotoDiv');
  thumbDiv.appendChild(image);

  var cell = row.insertCell();
  cell.appendChild(thumbDiv);

  /*
  var image = document.createElement('img');
  image.src = photoURL;
  image.width = 200;
  image.height = 200;

  var cell = row.insertCell();
  cell.appendChild(image);
  */
}
