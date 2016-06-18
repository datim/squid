'use-strict'
import React from "react";

import ActionButton from "./ActionButton"
import PhotoDisplay from "./PhotoDisplay"
import SearchStatus from "./SearchStatus"
import FilterBar from "./FilterBar"

/*
 * Display all of the buttons and the photo panel
 */
export default class PhotoPanel extends React.Component {

  constructor() {
    super();
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

  // Query all of the photos and get results as a JSON list
  queryPhotos() {
    const photoResultURL = 'http://localhost:8080/crawl/photos?filter=' + this.state.filter;
    console.log("Making query" + photoResultURL);
    var xhr = new XMLHttpRequest();
    xhr.open("GET", photoResultURL, false);
    xhr.send();
    return JSON.parse(xhr.responseText);
  }

  // erase all photos
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

    // get the latests photos
    var photoResults = this.queryPhotos();

    return (
      <div>
        <SearchStatus searchInProgress={this.state.isSearchInProgress} callback={this.searchFinished.bind(this)} start={Date.now()}/>
        <FilterBar keyStrokeEventCallback={this.filterCallback.bind(this)} />
        <br /> <br />
        <ActionButton callback={this.startSearch.bind(this)} message='Search' />
        <PhotoDisplay photos={photoResults} filter={this.state.filter} />
      </div>
    )
  }
}
