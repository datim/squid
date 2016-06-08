import React from "react";

export default class DownloadButton extends React.Component {

  constructor() {
    super()
    this.state = {clicked: false}
    this.image_height = '20';
    this.image_width = '20';
    this.download_img = 'images/download.jpg'
    this.saved_img = 'images/saved.png'
    this.button = this.setButton(false)
  }

  /*
   * Determine whether to display a 'download' or a 'saved' button
   */
  setButton(disabled = false) {
    if (disabled === false) {
      return this.getDownloadButton()

    } else {
      return this.getSavedButton()
    }
  }

    /*
     * Display a download button
     */
   getDownloadButton() {
     return <input type="image" src={this.download_img} alt="Submit" onClick={this.setClicked.bind(this)} width={this.image_width} height={this.image_height}/>
   }

   /*
    * Display a saved button
    */
   getSavedButton() {
     return <input type="image" src={this.saved_img}  width={this.image_width} height={this.image_height}/>
   }

   /*
    * Request that the image is downloaded
    */
   performImageDownload() {
     const DOWNLOAD_URL = 'http://localhost:8080/crawl/photos/download'
     var xhr = new XMLHttpRequest();
     xhr.open('POST', DOWNLOAD_URL, false);
     xhr.setRequestHeader("Content-type", "application/json");

     var param = JSON.stringify(this.props.photoData)
     console.log(param)
     xhr.send(param);
   }

   /*
    * Set the state of the button to be clicked
    */
   setClicked() {
     this.performImageDownload()
     this.button = this.setButton(true)
     this.setState({clicked: true})
   }

   render() {
     return(
        this.button
     )
   }
  }
