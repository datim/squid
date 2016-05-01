
// return the results in version
function getVersion() {

  // make the call
  var xhr = new XMLHttpRequest();
  var url = "http://localhost:8080/crawl/version";
  xhr.open("GET", url, false);
  xhr.send();

  var version = "Version " + xhr.responseText;
  version = version.fontcolor("#D3D3D3");

  document.write("<p align=right>");
  document.write(version);
  document.write("</p>");
}
