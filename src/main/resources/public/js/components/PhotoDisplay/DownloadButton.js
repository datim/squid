'use-strict'

import React from "react";

/*
 * Render the button used for saving images and displaying images as saved
 */
export default class DownloadButton extends React.Component {

  constructor() {
    super();
    this.state = {clicked: false};
    this.image_height = '20';
    this.image_width = '20';
    this.download_img = 'images/download.jpg'
    this.saved_img = 'images/saved.png'
  }

  componentWillMount() {
    // Called the first time the component is loaded right before the component is added to the page
    this.state.clicked = this.props.photoData.saved;
  }

  /*
   * Determine whether to display a 'download' or a 'saved' button
   */
  setButton(disabled = false) {
    if (this.state.clicked === false) {
      return this.getDownloadButton();

    } else {
      return this.getSavedButton();

    }
  }

  /*
   * Display a download button
   */
   getDownloadButton() {
     return <input type="image" src={this.download_img} alt="Submit" onClick={this.setClicked.bind(this)} width={this.image_width} height={this.image_height}/>;
   }

   /*
    * Display a saved button
    */
   getSavedButton() {
     return <input type="image" src={this.saved_img}  width={this.image_width} height={this.image_height}/>;
   }

   /*
    * Make rest call to download an image
    */
   performImageDownload() {
     const DOWNLOAD_URL = 'http://localhost:8080/crawl/photos/' + this.props.photoData.id + '/download';
     var xhr = new XMLHttpRequest();
     xhr.open('GET', DOWNLOAD_URL, false);
     xhr.setRequestHeader("Content-type", "application/json");

     var param = JSON.stringify(this.props.photoData)
     console.log(param)
     xhr.send();
   }

   /*
    * Set the state of the button to be clicked
    */
   setClicked() {
     this.performImageDownload();
     this.setState({clicked: true});
   }

   render() {
     var button = this.setButton();

     return(
        button
     )
   }
  }
