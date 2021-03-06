import React from "react";

/*
 *
 * Generate an image
 */
class IconPanelImage extends React.Component {

  // generate an icon image
  render() {
    var {src, height, width} = this.props;

    return(
      <img src={src} height={height} width={width}></img>
    )
  }
}

/*
 * Display a panel of icons
 */
export default class IconPanel extends React.Component {

  constructor() {
    super();
    this.state = {};

    // list of icons to display
    this.logos = [
      'images/react_logo.png',
      'images/spring_logo.png',
      'images/java_logo.png',
      'images/js_logo.jpg'
    ]
  }

  mouseOver() {
    console.log("icon mouse over!");
  }

  render() {

    var images = [];

    // generate images for icons
    for (var i = 0; i < this.logos.length; i++ ) {
      var klogo = this.logos[i];
      images.push(<IconPanelImage src={this.logos[i]} height='40' width='40' onmouseover={this.mouseOver.bind(this)}/>);
    }

    return(
      <div>
        {images}
      </div>
    )
  }
}
