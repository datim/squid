'use-strict'

import React from "react";

/*
 * Render an Image
 */
export default class ImageData extends React.Component {

  constructor(props) {
    super(props);
  }

  handleMouseOver() {
    console.log("Mouse over event for " + this.props.source.name);
  }

  handleMouseOut() {
    console.log("Mouse out event for " + this.props.source.name);
  }

  /*
   * generate the image tag
   */
  generateImage() {

    var imgStyle = {'width' : this.props.sourceWidth};

    var imageCell = <img src={this.props.source.url} style={imgStyle} label={this.props.source.name}></img>
    return imageCell;
  }

  render() {

    const imgTag = this.generateImage();

    return(
      <div onmouseover={this.handleMouseOver.bind(this)}>

        {imgTag}
      </div>
    )
  }
}
