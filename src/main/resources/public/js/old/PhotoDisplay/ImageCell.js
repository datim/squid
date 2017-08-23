'use-strict'

import React from "react";
import {Table, Column, Cell} from 'fixed-data-table';

import DownloadButton from "./DownloadButton"
import ImageData from "./ImageData"

/*
 * Render a cell within the photo data table
 */
export default class ImageCell extends React.Component {
  constructor(props) {
    super(props);
  }

  render() {
    var {rowIndex, data, url, height, width, columnIndex, ...props} = this.props;

    // get photo data for the correct row and column
    var photoData = data[rowIndex][columnIndex];
    var imgStyle = {'width' : width};

    // display the download button, image name, and the image itself
    return(
      <Cell {...props}>
        <div>
          <DownloadButton photoData={photoData} />
          {photoData.name}
          <ImageData source={photoData} sourceWidth={width}/>
        </div>
      </Cell>
    )
  }
}
