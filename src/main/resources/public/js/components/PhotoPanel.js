'use-strict'
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
    this.state = { refresh: false };

    this.photoResultURL = 'http://localhost:8080/crawl/photos';
    this.startSearchURL = 'http://localhost:8080/crawl/go';
    this.deletePhotosURL = 'http://localhost:8080/crawl/content';
  }

  /*
   * First erase all photos and then perform a new search
   */
   performNodeDiscovery() {

    // delete photos
    this.clearPhotos();

    var xhr = new XMLHttpRequest();
    xhr.open("GET", this.startSearchURL, false);
    xhr.send();
  }

  // Query all of the photos and get results as a JSON list
  queryPhotos() {
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

  /*
   * Trigger a state change
   */
  updatePhotos(photoList) {
    // update the state
    this.setState({refresh: true})
  }

  render() {

    // get the photos
    var photoResults = this.queryPhotos();

    return (
      <div>
        <Statistics count={photoResults.length}/>
        <ActionButton callMethod={this.performNodeDiscovery.bind(this)} response={this.updatePhotos.bind(this)} message='Search' />
        <ActionButton callMethod={this.queryPhotos.bind(this)} response={this.updatePhotos.bind(this)} message='Refresh'/>
        <PhotoDisplay photos={photoResults} />
      </div>
    )
  }
}
