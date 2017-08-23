'use-strict'
import React, {Component} from 'react';

import SideNavBarPages from "../../common/nav/SideNavBar"
import SearchButton from "./SearchButton"

/**
 * Represent the Image Search Page
 */
export default class ImagePage extends Component {

  constructor(props) {
    super(props);
  }

  /* Make an HTTP request */
  httpRequest(method, url) {
    var xhr = new XMLHttpRequest();
    xhr.open(method, url, false);
    xhr.send();
  }

  /* HTTP Post Requests */
  httpPostRequest(url) {
    httpRequest("POST", url);
  }

  /* HTTP Get Requests */
  httpGetRequest() {
    httpRequest("GET", url);
  }

  /* HTTP Delete Requests */
  httpDeleteRequest() {
    httpRequest("GET", url);
  }

  /* Generate the search field */
  createSearchBar() {
    var searchBar = <input type='text' name='search' maxLength={2048} size={100} height="48" id='searchURLInput'></input>;
    return searchBar;
  }

  render() {

    const searchBar = this.createSearchBar();

    return(
      <div>
        <h1>Image</h1>
        <SideNavBarPages/>
        <div id="searchBox">
          {searchBar}
          <SearchButton/>
        </div>
      </div>
    )
  }
}
