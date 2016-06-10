import React from "react";

/*
 * Report statistic counts
 */
export default class Statistics extends React.Component {

  constructor() {
    super();
    this.state = {};
  }

  componentWillMount() {
    // Called the first time the component is loaded right before the component is added to the page

  }

  componentDidMount(){
  // Called after the component has been rendered into the page
  }

  componentWillReceiveProps(nextProps){
    // Called when the props provided to the component are changed
  }

  componentWillUpdate(nextProps, nextState){
    // Called when the props and/or state change
  }

  componentWillUnmount(){
    // Called when the component is removed
  }

  // get the number of photos
  getPhotoCount() {

    const photoCountURL = 'http://localhost:8080/crawl/photos/count';

    // get the photo count
    var xhr = new XMLHttpRequest();
    xhr.open("GET", photoCountURL, false);
    xhr.send();
    return xhr.responseText;
  }

  // get the number of nodes
  getNodeCount() {

    const nodeCountURL = 'http://localhost:8080/crawl/nodes/count';

    // get the node count
    var xhr = new XMLHttpRequest();
    xhr.open("GET", nodeCountURL, false);
    xhr.send();
    return xhr.responseText;
  }

  render() {
    // display the photo and node counts
    var nodeCount = this.getNodeCount();
    var photoCount = this.getPhotoCount();

    return(
      <div>
        <p Alignment='right'> Photos {photoCount} <br /> Nodes: {nodeCount} </p>
      </div>
    )
  }
}
