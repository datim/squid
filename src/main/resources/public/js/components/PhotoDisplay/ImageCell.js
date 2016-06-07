import React from "react";
import {Table, Column, Cell} from 'fixed-data-table';

import DownloadButton from "./DownloadButton"


export default class ImageCell extends React.Component {
  constructor() {
    super()
  }

  render() {
    var {rowIndex, data, url, height, width, ...props} = this.props;

    return(
      <Cell {...props}>
        <img src={data[rowIndex].url} width={width} heigth={height} label={data[rowIndex].name}></img>
        {data[rowIndex].name}
        <DownloadButton photoData={data[rowIndex]} />
      </Cell>
    )
  }
}
