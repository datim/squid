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
  }

  /**
   * Respond to key-up events. Call the event handler with the event
   */
  onKeyDown(event) {

    // send value to caller only when enter key is pressed and there is a search text
    if (event.target.value && event.key === 'Enter') {
      this.props.searchKeyCallback(event.target.value);
    }
  }

  render() {

    var SearchStyle = {
      size: '90'
    }
    return(
        <div id='filterBarDiv'>
        SEARCH: <input type='text' name='search' maxlength={this.inputMaxLength} size={this.inputSize} id='searchBarInput' onKeyDown={this.onKeyDown.bind(this)}></input>
        </div>
    )
  }
}
