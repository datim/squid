import React from "react";
import {Table, Column, Cell} from 'fixed-data-table';

class ImageCell extends React.Component {
  constructor() {
    super()
    this.button = this.setDownloadButton(false)

    this.state = {clicked: false}
  }

  setDownloadButton(disabled = false) {
    if (disabled == true) {
      return <button disabled> Saved </button>
    }

    return <button onClick={this.setClicked.bind(this)}> Download </button>
  }

  setClicked() {
    this.setState({clicked: true})
    this.button = this.setDownloadButton(true)
  }

  render() {
    var {rowIndex, data, url, height, width, ...props} = this.props;
    return(
      <Cell {...props}>
        <img src={data[rowIndex].url} width={width} heigth={height} label={data[rowIndex].name}></img>
        {this.button}
        {data[rowIndex].name}
      </Cell>
    )
  }
}

/*
 * Render a Photo Image Column
 */
export default class PhotoDisplay extends React.Component {
  constructor() {
    super();
    this.imageWidth = 200;
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
        height={this.imageWidth * 4}
        width={this.imageWidth * 4}
        rowsCount={rows.length}
        rowHeight={this.imageWidth + 50}
        headerHeight={0}
      >
      <Column
        cell={<ImageCell data={rows}/>}
        width={this.imageWidth}
      />
      <Column
        cell={<ImageCell data={rows} height={this.imageWidth} width={this.imageWidth}/>}
        width={this.imageWidth}
      />
      <Column
        cell={<ImageCell data={rows} height={this.imageWidth} width={this.imageWidth}/>}
        width={this.imageWidth}
      />
      <Column
        cell={<ImageCell data={rows} height={this.imageWidth} width={this.imageWidth}/>}
        width={this.imageWidth}
      />
      </Table>
    )

  }
}
