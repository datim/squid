'use-strict'
import React from "react";

import ActionButton from "./ActionButton"
import PhotoDisplay from "./PhotoDisplay"
import SearchStatus from "./SearchStatus"
import FilterBar from "./FilterBar"

 /*
  * Responsible for displaying the panel of all status and results
  * related to images
  */
export default class PhotoPanel extends React.Component {

  constructor(props) {
    super(props);
    this.state = { isSearchInProgress: false, filter: '' };
  }

  /*
   * Begin the search for new pages
   */
  startPageCrawl() {
    const startSearchURL = 'http://localhost:8080/crawl/go';

    var xhr = new XMLHttpRequest();
    xhr.open("GET", startSearchURL, false);
    xhr.send();
  }


  /*
   * Request that server erase all photos
   */
  clearPhotos() {
    var deletePhotosURL = 'http://localhost:8080/crawl/content';

    var xhr = new XMLHttpRequest();
    xhr.open("DELETE", deletePhotosURL, false);
    xhr.send();
  }

  /**
   * Check the status of a search.  If the search is not complete,
   * continue searching
   */
  searchFinished() {
    this.setState({isSearchInProgress: false})
  }

  /*
   * Trigger a state change after start search has been enabled
   */
  startSearch() {
    console.log("Start Search");

    // start search
    this.clearPhotos();
    this.startPageCrawl();

    // update the state to trigger refresh
    this.setState({isSearchInProgress: true})
  }

  /*
   * perform key stroke
   */
  filterCallback(filterInput) {
    console.log ("filterCallback called");
    this.setState({filter: filterInput});
  }

  render() {

    return (
      <div>
        <SearchStatus searchInProgress={this.state.isSearchInProgress} callback={this.searchFinished.bind(this)} start={Date.now()}/>
        <FilterBar keyStrokeEventCallback={this.filterCallback.bind(this)} />
        <br /> <br />
        <ActionButton callback={this.startSearch.bind(this)} message='Search' />
        <PhotoDisplay filter={this.state.filter} />
      </div>
    )
  }
}
