'use-strict'
import React from "react";

/*
 * Provide a text input that triggers a search for its value when enter is pressed
 */
export default class SearchBar extends React.Component {

  constructor(props) {
    super(props);
    this.inputSize = 100;
    this.inputMaxLength = 1000;
    this.defaultUserId = 1;
    this.state = {initialURL : '', initialState : true}
  }

  componentWillMount() {
    // Called the first time the component is loaded right before the component is added to the page

    // get initial URL that was last searched
    this.state.initialURL = this.getInitialSearchField(this.defaultUserId);
  }

  componentDidMount() {
    // Called after the component has been rendered into the page
  }

  componentDidUpdate() {
    // called after balah
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

  getInitialSearchField(userId) {
    const searchParams = 'http://localhost:8080/crawl/search/parameters/' + userId;
    var xhr = new XMLHttpRequest();
    xhr.open("GET", searchParams, false);
    xhr.send();
    const params = JSON.parse(xhr.responseText);

    return params.searchURL;
  }

  /**
   * Respond to key-up events. Call the event handler with the event
   */
  onEnterKeyDown(event) {
    // send value to caller only when enter key is pressed and there is a search text
    if (event.key === 'Enter') {
      this.props.searchKeyCallback(event.target.value);
    }

    // force a re-render
    this.forceUpdate();
  }

  /**
   * Generate the search bar
   */
  createSearchBar() {

    var searchBar = <input type='text' name='search' maxLength={this.inputMaxLength} size={this.inputSize} id='searchBarInput' onKeyDown={this.onEnterKeyDown.bind(this)}></input>;

    if (this.state.initialState) {
      // draw the search bar with the initial URL
      searchBar = <input type='text' value = {this.state.initialURL} name='search' maxLength={this.inputMaxLength} size={this.inputSize} id='searchBarInput' onKeyDown={this.onEnterKeyDown.bind(this)}></input>;

      // update the state so that we don't draw the initial url again
      this.state.initialState = false;
    }
    return searchBar;
  }

  render() {

    const searchBar = this.createSearchBar();
    return(
        <div id='filterBarDiv'>
        SEARCH: {searchBar}
        </div>
    )
  }
}
