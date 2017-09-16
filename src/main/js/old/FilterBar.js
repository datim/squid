'use-strict'
import React from "react";

/*
* Provide a text input that triggers a result filter for its value
* when any key is pressed
*/
export default class FilterBar extends React.Component {

  constructor(props) {
    super(props);
    this.inputSize = 100;
    this.inputMaxLength = 1000;
  }

  /**
   * Respond to key-up events. Call the event handler with the event
   */
  keyUp(event) {
    // send value to caller for state update
    this.props.keyStrokeEventCallback(event.target.value);
  }

  render() {

    return(
      <div id='filterBarDiv'>
        FILTER: <input type='text' id='filterBarInput' name='filter' maxLength={this.inputMaxLength} size={this.inputSize} onKeyUp={this.keyUp.bind(this)}></input>
      </div>
    )
  }
}
