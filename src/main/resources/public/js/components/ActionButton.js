import React from "react";
/*
 * Button to trigger reporting of results
 */
 // Reference: https://facebook.github.io/react/docs/state-and-lifecycle.html
export default class ActionButton extends React.Component {

  constructor(props) {
    super(props);
    this.state = {};
    this.handleClick = this.handleClick.bind(this);
  }

  handleClick() {
    this.props.callback();
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
