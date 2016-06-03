import React from "react";
/*
 * Button to trigger reporting of results
 */
 // Reference: https://facebook.github.io/react/docs/interactivity-and-dynamic-uis.html

export default class DisplayButton extends React.Component {

  constructor() {
    super();
    this.state = {};

    this.handleClick = this.handleClick.bind(this);
  }

  handleClick() {
    console.log("Searching For Photos!");

    var photoJSON = this.props.callMethod();
    console.log(photoJSON);

    // update the results
    this.props.response(photoJSON);
  }

  render() {
    // display
    return(
      <button onClick={this.handleClick.bind(this)}>
        Display
      </button>
    )
  }
}
