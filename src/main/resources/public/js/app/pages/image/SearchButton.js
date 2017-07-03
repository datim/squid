'use-strict'
import React, {Component} from 'react';

/**
 * Define the search button for a pages
 */
export default class SearchButton extends Component {

  constructor(props) {
    super(props);
    this.start_image = '/images/start_search.png';
    this.stop_image = '/images/stop_search.png';
    this.state = {isToggleOn: true};

    // handle clicks through 'handleclick' method
    this.handleClick = this.handleClick.bind(this);
    }

    // update on a click
    handleClick() {
      this.setState(prevState => ({
        isToggleOn: !prevState.isToggleOn
      }));
    }

  render() {
    const displayImage = this.start_image;

    return(
      <input onClick={this.handleClick} type="image" src={this.state.isToggleOn ? this.start_image : this.stop_image} alt="Submit"/>
    )
  }
}
