'use-strict'
import React from "react";

export default class SearchStatus extends React.Component {

  constructor(props) {
    super(props);

    this.state = {elapsed: 0}
    this.ms = 1000;       // delay time in miliseconds

    // initial status display
    this.statusResults = undefined;
    this.timer = undefined;
  }

  /*
   * Query the status of the search
   */
  getStatus() {
    const statusURL = 'http://localhost:8080/crawl/url/search/status';
    var xhr = new XMLHttpRequest();
    xhr.open("GET", statusURL, false);
    xhr.send();
    var results = JSON.parse(xhr.responseText);

    return results;
  }

   /*
    * Update the state of the display
    */
  setDisplay() {
    var display =  "Pages: " + String(this.statusResults.nodeCount) + " Images: " + String(this.statusResults.imageCount);

    if (this.statusResults.status !== "Complete") {
      display.concat(" State: " + this.statusResults.status);
    }

    return display;
  }


  checkSearchStatus() {
    if (this.timer !== undefined) {
      console.log("Cannot update, search already in progress");
      return;
    }

    if (this.props.searchInProgress === true) {
      console.log("SearchStatus: Search is in progress!");

      // set a repeating timer for this.ms milliseconds
      this.timer = setInterval(this.tick.bind(this), this.ms);

      // update the state to re-render
    } else {
      console.log("SearchStatus: Search is not in progress!")
    }
  }

  /*
   * called every time the timer fires. Determine whether we are going to update
   * or complete.
   */
  tick() {
    console.log("Tick!")
    this.statusResults = this.getStatus();

    if (this.statusResults.status !== "In Progress") {
      console.log("Tick: status not in progress.clearing interval");
      // the search is not currently running. Disable further checks and report back
      clearInterval(this.timer);
      this.timer = undefined;
      this.props.callback(this.statusResults.status);

    } else {
      console.log("Tick: status  in progress");
      // we're still searching. Update the state so that we can display progress
      this.setState({elapsed: new Date() - this.props.start});
    }
  }

  componentWillMount() {
    // Called the first time the component is loaded right before the component is added to the page
    console.log("Component will mount!");

    // perform first status check
    this.statusResults = this.getStatus();
  }

  componentDidMount() {
    // Called after the component has been rendered into the page
    console.log("Component did mount!");
    this.checkSearchStatus();
  }

  componentDidUpdate() {
    // called after balah
    console.log("Component did update!");
    this.checkSearchStatus();
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

  render() {
    var display = this.setDisplay();

    return(
      <div>
        <p>{display}</p>
      </div>
    )
  }
}
