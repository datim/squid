import React from "react";
/*
 * Display all of the buttons and the photo display results
 */

 import DisplayButton from "./DisplayButton"
 import PhotoResults from "./PhotoResults"

export default class PhotoPanel extends React.Component {

  constructor() {
    super();
    this.state = {
      photos: [],
      length: 0
    };

    this.photoResultURL = 'http://localhost:8080/crawl/photos';
  }

  // Query all of the photos and get results
  getPhotos() {
    var xhr = new XMLHttpRequest();
    xhr.open("GET", this.photoResultURL, false);
    xhr.send();

    return JSON.parse(xhr.responseText);
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
      <DisplayButton callMethod={this.getPhotos.bind(this)} response={this.updatePhotos.bind(this)}/>
      <PhotoResults photos={this.state.photos} photosPerRow={this.props.photosPerRow}/>
      </div>
    )
  }
}
