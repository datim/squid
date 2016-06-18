'use-strict'
import React from "react";

export default class FilterBar extends React.Component {

  constructor(props) {
    super(props);
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
      <div id='filterBar'>
        <input type='text' name='filter' onKeyUp={this.keyUp.bind(this)}></input>
      </div>
    )
  }
}
