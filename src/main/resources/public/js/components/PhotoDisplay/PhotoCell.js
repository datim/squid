'use-strict'

import React from "react";
import {Table, Column, Cell} from 'fixed-data-table';

import DownloadButton from "./DownloadButton"

/*
 * Render a cell within the photo data table
 */
export default class PhotoCell extends React.Component {
  constructor() {
    super();
  }
  //<img src={photoData.url} width={width} heigth={height} label={photoData.name}></img>

  render() {
    var {rowIndex, data, url, height, width, columnIndex, ...props} = this.props;

    // get photo data for the correct row and column
    var photoData = data[rowIndex][columnIndex];

    var imgStyle = {'width' : width};

    return(
      <Cell {...props}>
        <div>
        <DownloadButton photoData={photoData} />
        {photoData.name}
        <img src={photoData.url} style={imgStyle} label={photoData.name}></img>

        </div>
      </Cell>
    )
  }
}
