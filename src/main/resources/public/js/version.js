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
  version = version.fontcolor("#D3D3D3");

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

  // now fetch photos
  xhr.open("GET", "http://localhost:8080/crawl/photos", false);
  xhr.send();

  var photoResults = JSON.parse(xhr.responseText);

  var table = document.getElementById("PhotoResultsTable");

  // write a table of photos
  var i = 0;
  for (i = 0; i < photoResults.length; /*i++*/ ) {
    var row = table.insertRow();
    var cell1 = row.insertCell();

    // create the image and assign it to a cell
    var image = document.createElement('img');
    image.src = photoResults[i].url;
    cell1.appendChild(image);
    //cell1.innerHTML = image;

    i = i + 1;

    // add a second row if there is enough items
    if (i < photoResults.length)
      var cell2 = row.insertCell();

      // insert image into row two
      var image2 = document.createElement('img');
      image2.src = photoResults[i].url;
      cell2.appendChild(image2);
      i = i + 1;
    }
}
