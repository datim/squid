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

    // there is already a timer in progress. Don't start a new one
    if (this.timer !== undefined) {
      return;
    }

    if (this.props.searchInProgress === true) {
      console.log("SearchStatus: starting timer!");

      // set a repeating timer for this.ms milliseconds
      this.timer = setInterval(this.tick.bind(this), this.ms);

      // update the state to re-render
    }
  }

  /*
   * called every time the timer fires. Determine whether we are going to update
   * or complete.
   */
  tick() {
    this.statusResults = this.getStatus();

    if (this.statusResults.status !== "In Progress") {
      console.log("Time finished. State is " + this.statusResults.status);
      // the search is not currently running. Disable further checks and report back
      clearInterval(this.timer);
      this.timer = undefined;
    }

    // update the state
    this.props.callback();
  }

  componentWillMount() {
    // Called the first time the component is loaded right before the component is added to the page

    // perform first status check
    this.statusResults = this.getStatus();
  }

  componentDidMount() {
    // Called after the component has been rendered into the page
    this.checkSearchStatus();
  }

  componentDidUpdate() {
    // called after balah
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
