'use-strict'

import React from "react";
import {Table, Column, Cell} from 'fixed-data-table';

import ImageCell from "./PhotoDisplay/ImageCell"

/*
 * Render the table display of photos
 */
export default class PhotoDisplay extends React.Component {
  constructor() {
    super();
    this.imageDimension = 300;
    this.numColumns = 4;
  }

  /*
   * convert a list of photo objets into a two dimensional list
   * Each row in the list is a nested list of numColumn items
   */
  createPhotoColumns(photoList) {

    var columnizedData = [];
    var rowData = [];
    for(var i = 0; i < photoList.length; i++) {

      var photoData = photoList[i];
      rowData.push(photoData);

      if (i % this.numColumns === 0 && i !== 0) {
        // if we've pushed four items into the row, then time to add to
        // the column object and start over
        columnizedData.push(rowData);
        rowData = [];
      }
    }

    return columnizedData;
  }

  render() {

    const rows = this.createPhotoColumns(this.props.photos);

    return(
      <Table
        height={this.imageDimension * 4}
        width={this.imageDimension * 4}
        rowsCount={rows.length}
        rowHeight={this.imageDimension + 50}
        headerHeight={0}
      >
      <Column
        cell={<ImageCell data={rows} height={this.imageDimension} width={this.imageDimension} columnIndex='0'/>}
        width={this.imageDimension}
      />
      <Column
        cell={<ImageCell data={rows} height={this.imageDimension} width={this.imageDimension} columnIndex='1'/>}
        width={this.imageDimension}
      />
      <Column
        cell={<ImageCell data={rows} height={this.imageDimension} width={this.imageDimension} columnIndex='2'/>}
        width={this.imageDimension}
      />
      <Column
        cell={<ImageCell data={rows} height={this.imageDimension} width={this.imageDimension} columnIndex='3'/>}
        width={this.imageDimension}
      />
      </Table>
    )

  }
}
