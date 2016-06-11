import React from "react";

/*
 * Report Squid version
 */
export default class SquidVersion extends React.Component {

  constructor() {
    super();
    this.version = 0.0;
    this.state = {};
  }

  componentWillMount() {
    // Called the first time the component is loaded right before the component is added to the page
    this.queryVersion();
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

  queryVersion() {
    // query for the version of the program
    const url = `http://localhost:8080/crawl/version`;
    var xhr = new XMLHttpRequest();
    xhr.open("GET", url, false);
    xhr.send();

    // save the version
    this.version = xhr.responseText;
  }

  render() {
    // display
    return(
      <div>
        <p> Squid {this.version} </p>
      </div>
    )
  }
}
