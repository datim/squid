'use-strict'

import React from "react";
import {Table, Column, Cell} from 'fixed-data-table';

import DownloadButton from "./DownloadButton"

/*
 * Render a cell within the photo data table
 */
export default class ImageCell extends React.Component {
  constructor() {
    super();
  }

  render() {
    var {rowIndex, data, url, height, width, columnIndex, ...props} = this.props;

    // get photo data for the correct row and column
    var photoData = data[rowIndex][columnIndex];

    return(
      <Cell {...props}>
        <img src={photoData.url} width={width} heigth={height} label={photoData.name}></img>
        {photoData.name}
        <DownloadButton photoData={photoData} />
      </Cell>
    )
  }
}
