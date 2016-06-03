import React from "react";

/*
 * Report Squid photo and node statistics
 */
export default class Statistics extends React.Component {

  constructor() {
    super();
    this.state = {};
    this.photoCount = 0;
    this.nodeCount = 0;
  }

  componentWillMount() {
    // Called the first time the component is loaded right before the component is added to the page
    this.getStatistics();
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

  getStatistics() {
    // look up statistics
    // make the call
    const photoCountURL = 'http://localhost:8080/crawl/photos/count';
    const nodeCountURL = 'http://localhost:8080/crawl/nodes/count';

    // get the photo count
    var xhr = new XMLHttpRequest();
    xhr.open("GET", photoCountURL, false);
    xhr.send();
    this.photoCount = xhr.responseText;

    // get the node count
    xhr.open("GET", nodeCountURL, false);
    xhr.send();
    this.nodeCount = xhr.responseText;
  }

  render() {
    // display the photo and node counts
    return(
      <div>
        <p Alignment='right'> Photos {this.photoCount} <br /> Nodes: {this.nodeCount} </p>
      </div>
    )
  }
}
