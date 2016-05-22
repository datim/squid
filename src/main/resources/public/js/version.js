"use strict";

//
// return the results in version
//
function getSquidVersion() {

  // make the call
  var xhr = new XMLHttpRequest();
  var url = "http://localhost:8080/crawl/version";
  xhr.open("GET", url, false);
  xhr.send();

  var version = "<p align=right> Squid " + xhr.responseText + "</p>";

  document.write(version);
}

//
// Delete all node and photo content
//
function deleteAllContent() {
  // make the call
  var xhr = new XMLHttpRequest();
  xhr.open("DELETE", "http://localhost:8080/crawl/content", false);
  xhr.send();
}

//
// getCounts
//
// Get the number of photos and nodes that are stored in the system
//
function getContentCounts() {

  // make the call
  var xhr = new XMLHttpRequest();
  xhr.open("GET", "http://localhost:8080/crawl/photos/count", false);
  xhr.send();
  var photoCount = xhr.responseText;

  xhr.open("GET", "http://localhost:8080/crawl/nodes/count", false);
  xhr.send();
  var nodeCount = xhr.responseText;

  var version = "<p align=right> Photos: " + photoCount + "<br> Nodes: " + nodeCount + "</p>";

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

  var photoCaption = document.createElement('figcaption');
  photoCaption.innerHTML = photoResult.name;

  var photoFigure = document.createElement('figure');
  photoFigure.appendChild(image);
  photoFigure.appendChild(photoCaption);

  var thumbDiv = document.createElement('div');
  thumbDiv.appendChild(photoFigure);

  var cell = row.insertCell();
  cell.appendChild(thumbDiv);
}
