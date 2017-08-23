'use-strict'
import React, {Component} from 'react';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { getStore } from "../../store/ManageStore";
import * as searchActions from "../../actions/Actions";

// Search Button Component
class SearchButton extends Component {

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

    // trigger action for button change
    this.props.actions.toggleSearchButton()

    // update button state
    this.setState({
      isToggleOn: !this.state.isToggleOn }
    )
  }

  render() {
    const displayImage = this.start_image;

    return(
      <input onClick={this.handleClick} type="image" src={this.state.isToggleOn ? this.start_image : this.stop_image} alt="Submit"/>
    )
  }
}

// map state to properties
const mapStateToProps = (state, ownProps) => {
  return {
    searchState: state.searchState
  }
};

// map all actions to properties
const mapDispatchToProps = (dispatch) => {
  return {
    actions: bindActionCreators(searchActions, dispatch)
  }
};

// connect state to class
export default connect(mapStateToProps, mapDispatchToProps)(SearchButton);