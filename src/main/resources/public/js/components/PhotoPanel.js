import React from "react";


import PhotoResults from "./PhotoResults"
import ActionButton from "./ActionButton"
import Statistics from "./Statistics"
import PhotoDisplay from "./PhotoDisplay"

/*
 * Display all of the buttons and the photo panel
 */
export default class PhotoPanel extends React.Component {

  constructor() {
    super();
    this.state = {
      length: 0
    };

    this.photoResultURL = 'http://localhost:8080/crawl/photos';
    this.startSearchURL = 'http://localhost:8080/crawl/go';
    this.deletePhotosURL = 'http://localhost:8080/crawl/content';
    this.photos = [];
  }

  componentWillMount() {
    // Called the first time the component is loaded right before the component is added to the page
    this.photos = this.getPhotos();
    this.setState({length: this.photos.length})
  }

  // perform a search for photos and nodes
  performNodeDiscovery() {
    var xhr = new XMLHttpRequest();
    xhr.open("GET", this.startSearchURL, false);
    xhr.send();
  }

  // Query all of the photos and get results
  getPhotos() {
    var xhr = new XMLHttpRequest();
    xhr.open("GET", this.photoResultURL, false);
    xhr.send();

    return JSON.parse(xhr.responseText);
  }

  // erase all photos
  clearPhotos() {
    var xhr = new XMLHttpRequest();
    xhr.open("DELETE", this.deletePhotosURL, false);
    xhr.send();
  }

  updatePhotos(photoList) {
    // update the state, triggering a re-render of the page
    this.photos = photoList;
    this.setState({length: photoList.length})
  }

  render() {
    console.log("refesh photo panel");
    return (
      <div>
        <Statistics />
        <ActionButton callMethod={this.performNodeDiscovery.bind(this)} message='Search' />
        <ActionButton callMethod={this.getPhotos.bind(this)} response={this.updatePhotos.bind(this)} message='Refresh'/>
        <ActionButton callMethod={this.clearPhotos.bind(this)} message='Delete' />
        <PhotoDisplay photos={this.photos} />
      </div>
    )
  }
}
