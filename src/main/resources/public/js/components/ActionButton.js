import React from "react";
/*
 * Button to trigger reporting of results
 */
 // Reference: https://facebook.github.io/react/docs/interactivity-and-dynamic-uis.html

export default class ActionButton extends React.Component {

  constructor() {
    super();
    this.state = {};
    this.handleClick = this.handleClick.bind(this);
  }

  handleClick() {
    console.log("Searching For Photos!");

    var result = this.props.callMethod();

    // report results, if any are returned
    if (this.props.response != null) {
      this.props.response(result);
    }
  }

  render() {

    // display
    return(
      <button onClick={this.handleClick.bind(this)}>
        {this.props.message}
      </button>
    )
  }
}
