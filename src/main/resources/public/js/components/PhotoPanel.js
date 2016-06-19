'use-strict'
import React from "react";

import ActionButton from "./ActionButton"
import PhotoDisplay from "./PhotoDisplay"
import SearchStatus from "./SearchStatus"
import FilterBar from "./FilterBar"
import SearchBar from "./SearchBar"

 /*
  * Responsible for displaying the panel of all status and results
  * related to images
  */
export default class PhotoPanel extends React.Component {

  constructor(props) {
    super(props);
    this.state = { isSearchInProgress: false, filter: '', searchValue: ''};
  }

  /*
   * Begin the search for new pages
   */
  startPageCrawl(searchInput) {
    const startSearchURL = 'http://localhost:8080/crawl/search/search?discoverUrl=' + searchInput;

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
  startSearch(searchInput) {
    console.log("Start Search");

    // start search
    this.clearPhotos();
    this.startPageCrawl(searchInput);

    // update the state to trigger refresh
    this.setState({isSearchInProgress: true})
  }

  /*
   * Called when user prses 'enter' on a new search
   */
   searchKeyEvent(searchInput) {
     console.log("Searching for " + searchInput);

     this.startSearch(searchInput);

     // update the state
     this.setState({searchValue: searchInput, isSearchInProgress: true});
   }
  /*
   * perform key stroke
   */
  filterKeyEvent(filterInput) {
    console.log ("filterCallback called");
    this.setState({filter: filterInput});
  }

  render() {

    return (
      <div>
        <SearchBar searchKeyCallback={this.searchKeyEvent.bind(this)} />
        <br />
        <FilterBar keyStrokeEventCallback={this.filterKeyEvent.bind(this)} />
        <br />
        <SearchStatus searchInProgress={this.state.isSearchInProgress} callback={this.searchFinished.bind(this)} start={Date.now()}/>
        <br /> <br />
        <PhotoDisplay filter={this.state.filter} />
      </div>
    )
  }
}
