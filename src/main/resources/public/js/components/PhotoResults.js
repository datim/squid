import React from "react";

/*
 * Render a Photo Image Column
 */
class PhotoResultImageColumn extends React.Component {

  render() {
    var { imgURL, dimensions } = this.props;

    // render the column containing an image
    return (
      <td>
        <img src={imgURL} width={dimensions}></img>
      </td>
    )
  }
}

/*
 * Render a photo image row, consisting of multiple columns
 */
class PhotoResultImageRow extends React.Component {

  render() {
    var {images, dimensions} = this.props;
    var columns = [];

    // generate a list of columns that this row will contain
    for (var i = 0; i < images.length; i++) {
      var image = images[i];
      columns.push(<PhotoResultImageColumn imgURL={image} dimensions={dimensions} />);
    }

    // render the row
    return (
      <tr>
        {columns}
      </tr>
    )
  }
}

/*
 * Display a table of photo results
 */
 // Reference: https://facebook.github.io/react/docs/interactivity-and-dynamic-uis.html
export default class PhotoResults extends React.Component {

  constructor() {
    super();
  }

  render() {
    // display photo results
    var {photos, photosPerRow} = this.props;
    var rows = [];
    var imageRowList = [];

    // generate the rows of photos
    for (var i = 0; i < photos.length; i++) {

      imageRowList.push(photos[i].url);

      if (i % photosPerRow == 0) {
        imageRowList = [];  // reset
        rows.push(<PhotoResultImageRow images={imageRowList} dimensions='250' />);
      }
      //var photoURL = photos[i].url;
      //rows.push(<PhotoResultImageRow imgUrl={photoURL} dimensions='250' />);
    }

    return (
      <div>
        <p> Number of photos to display: {photos.length} </p>
        <table cellpadding = '5'>
          <tbody>
            {rows}
          </tbody>
        </table>
      </div>
    )
  }
}
