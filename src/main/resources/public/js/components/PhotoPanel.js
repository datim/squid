import React from "react";
/*
 * Display all of the buttons and the photo display results
 */
import PhotoResults from "./PhotoResults"
import ActionButton from "./ActionButton"
import Statistics from "./Statistics"

export default class PhotoPanel extends React.Component {

  constructor() {
    super();
    this.state = {
      photos: [],
      length: 0
    };

    this.photoResultURL = 'http://localhost:8080/crawl/photos';
    this.startSearchURL = 'http://localhost:8080/crawl/go';
    this.deletePhotosURL = 'http://localhost:8080/crawl/content';
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
    this.setState({photos: photoList, length: photoList.length})
  }

  render() {
    // rend
    console.log('render!');

    return (
      <div>
        <Statistics />
        <ActionButton callMethod={this.performNodeDiscovery.bind(this)} message='Search' />
        <ActionButton callMethod={this.getPhotos.bind(this)} response={this.updatePhotos.bind(this)} message='Display'/>
        <ActionButton callMethod={this.clearPhotos.bind(this)} message='Delete' />
        <PhotoResults photos={this.state.photos} photosPerRow={this.props.photosPerRow}/>
      </div>
    )
  }
}
