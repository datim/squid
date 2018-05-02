'use-strict'
/**
 * Search bar and buttons
 */

import React, {Component} from 'react';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import * as searchActions from '../../actions/Actions';
import { isStateStopped } from '../../common/StateHelper';
import { getServerURL, urlGetRequest } from '../../common/Utils';
import * as globals from '../../common/GlobalConstants';

const defaultSearchInputValue = "Please Enter a Search";

// Render Search Box
const SearchBox = (props) => {
    const { onClick, defaultValue, keyHandler } = props;

    return (
        <input type='text' name='search' onClick={onClick} defaultValue={defaultValue} maxLength={2048} size={100} height="48" id='searchURLInput' onKeyUp={keyHandler.bind(this)}/>
    );
}

// Render search button
const SearchButton = (props) => {
    const { onClick, display_img } = props;

    return (
        <input onClick={onClick} type="image" src={display_img} alt="Submit"/>
    );
}

/**
 * Container for Search Inputs
 */
class SearchContainer extends Component {

    constructor(props) {
        super(props);
        this.start_image = '/images/start_search.png';
        this.stop_image = '/images/stop_search.png';
        this.state = {
            searchInput: defaultSearchInputValue
        };

        // handle clicks through 'handleclick' method
        this.handleClick = this.handleClick.bind(this);
        this.handleSearchInputEvent = this.handleSearchInputEvent.bind(this);
        this.handleSearchInputBoxClick = this.handleSearchInputBoxClick.bind(this);
    }

    // when page mounts, check if there was a previous search
    componentDidMount() {
        // Check if a query was run
        const lastSearchURL = getServerURL() + '/' + globals.SEARCH_ROOT + '/last';

        urlGetRequest(lastSearchURL).then ( (lastQuery) => {
            console.log('last search was', lastQuery);
            if (lastQuery.id != '0') {
                // initialize results for the last search
                this.props.actions.initLastSearchResults(lastQuery);
            }
        });
    }

    // upon clicking the search button, trigger a search
    handleClick() {
        console.log("Search button pressed. Input is '" + this.state.searchInput + "'");

        // dispatch a search
        this.props.actions.toggleSearch(
            this.state.searchInput,
            this.props.queryState.state,
            this.props.queryState.current_query_id
            );
    }

    // Update the search input state from the text bar every time a key is pressed
    handleSearchInputEvent(event) {

        this.setState({
            searchInput: event.target.value 
        })
    }
    
    // trigger when mouse click on search input box
    handleSearchInputBoxClick(event) {

        if (event.target.value == defaultSearchInputValue) {
            // the input search was clicked, and default value is present. Clear it in 
            // anticipation of use input
            
            event.target.value = "";
            this.setState({
                searchInput: "" 
            })
        }
    }

    render() {
        const displayStopImage = !isStateStopped(this.props.queryState.state);

        return(
            <div id="searchpanel">
                <SearchBox 
                    keyHandler={this.handleSearchInputEvent} 
                    onClick={this.handleSearchInputBoxClick}
                    defaultValue = {this.state.searchInput}/>
                <SearchButton 
                    display_img = {displayStopImage ? this.stop_image : this.start_image}
                    onClick={this.handleClick}/>
            </div>
        )
  }
}

// map state to properties
const mapStateToProps = (state, ownProps) => {
    return {
        queryState: state.queryState.searchState
    }
  };
  
// map all actions to properties
const mapDispatchToProps = (dispatch) => {
    return {
      actions: bindActionCreators(searchActions, dispatch)
    }
};
  
// connect state to class
export default connect(mapStateToProps, mapDispatchToProps)(SearchContainer);

// FIXME - add propTypes