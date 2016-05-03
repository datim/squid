//
// return the results in version
//
function getVersion() {

  // make the call
  var xhr = new XMLHttpRequest();
  var url = "http://localhost:8080/crawl/version";
  xhr.open("GET", url, false);
  xhr.send();

  var version = "Squid " + xhr.responseText;
  version = version.fontcolor("#D3D3D3");

  document.write("<p align=right>");
  document.write(version);
  document.write("</p>");
}

//
// start a search for nodes and display results
//
function startSearch() {

  var xhr = new XMLHttpRequest();
  var url = "http://localhost:8080/crawl/go";
  xhr.open("GET", url, false);
  xhr.send();

  xhr.open("GET", "http://localhost:8080/crawl/photos", false);
  xhr.send();

  var photoResults = JSON.parse(xhr.responseText);

  var table = document.getElementById("photoResultsTable");

  // write a table of photos
  for (i = 0; i < photoResults.length; /*i++*/ ) {
    var row = table.insertRow(0);
    var cell1 = row.insertCell(0);
    cell1.innerHTML = "<td><img src=" + photoResults[i].url + ">";

    i = i + 1;

    // add a second row if there is enough items
    if (i < photoResults.length)
      var cell2 = row.insertCell(1);
      cell2.innerHTML = "<td><img src=" + photoResults[i].url + " alt=" + photoResults[i].name + "></td>";
      i = i + 1;
    }
}
