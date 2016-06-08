'use-strict'

import React from "react";
import {Table, Column, Cell} from 'fixed-data-table';

import PhotoCell from "./PhotoDisplay/PhotoCell"

/*
 * Render the table display of photos
 */
export default class PhotoDisplay extends React.Component {
  constructor() {
    super();
    this.imageWidth = 300;
    this.imageHeight = 300;
    this.cellWidth = 300;
    this.cellHeight = 300;
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
        height={this.cellHeight * 2}  // table width
        width={this.cellWidth * 4}    // table height
        rowsCount={rows.length}
        rowHeight={this.cellHeight}
        headerHeight={0}
      >
      <Column
        cell={<PhotoCell data={rows} height={this.imageHeight} width={this.imageWidth} columnIndex='0'/>}
        width={this.cellWidth}
      />
      <Column
        cell={<PhotoCell data={rows} height={this.imageHeight} width={this.imageWidth} columnIndex='1'/>}
        width={this.cellWidth}
      />
      <Column
        cell={<PhotoCell data={rows} height={this.imageHeight} width={this.imageWidth} columnIndex='2'/>}
        width={this.cellWidth}
      />
      <Column
        cell={<PhotoCell data={rows} height={this.imageHeight} width={this.imageWidth} columnIndex='3'/>}
        width={this.cellWidth}
      />
      </Table>
    )

  }
}
