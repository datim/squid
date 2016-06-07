import React from "react";
import {Table, Column, Cell} from 'fixed-data-table';

import ImageCell from "./PhotoDisplay/ImageCell"

/*
 * Render a Photo Image Column
 */
export default class PhotoDisplay extends React.Component {
  constructor() {
    super();
    this.imageDimension = 300;
  }

  /*
  rowGetter={function(rowIndex) {return rows[rowIndex]; }}>
  <Column dataKey="url" width={400} label="URL" />
  <Column dataKey="name" width={100} label="Name" />
  <Column cell={<CreateImage  />} width={200} label="Image" />
  */

  render() {
    //var rows = this.data;
    var rows = this.props.photos;

    return(
      <Table
        height={this.imageDimension * 4}
        width={this.imageDimension * 4}
        rowsCount={rows.length}
        rowHeight={this.imageDimension + 50}
        headerHeight={0}
      >
      <Column
        cell={<ImageCell data={rows} height={this.imageDimension} width={this.imageDimension}/>}
        width={this.imageDimension}
      />
      <Column
        cell={<ImageCell data={rows} height={this.imageDimension} width={this.imageDimension}/>}
        width={this.imageDimension}
      />
      <Column
        cell={<ImageCell data={rows} height={this.imageDimension} width={this.imageDimension}/>}
        width={this.imageDimension}
      />
      <Column
        cell={<ImageCell data={rows} height={this.imageDimension} width={this.imageDimension}/>}
        width={this.imageDimension}
      />
      </Table>
    )

  }
}
