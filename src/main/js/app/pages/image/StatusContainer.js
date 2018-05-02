'use-strict'

/**
 * Container to maintain search status
 */
import React, {Component} from 'react';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import * as searchActions from "../../actions/Actions";
import { isStateRunning } from "../../common/StateHelper";
import * as globals from "../../common/GlobalConstants";
import * as searchStates from '../../actions/SearchStates';

const rp = require('request-promise');
const noResultMessage = "No Search Results";

/**
 * Container for Search Status
 */
class StatusContainer extends Component {

    constructor(props) {
        super(props);
    }

    // get the initial status when page mounts if status is not -1
    componentDidMount() {
        if (this.props.queryState.state == searchStates.SEARCH_RUNNING) {
            // search status is running, request another status check
            this.props.actions.delayedCheckSearchStatus(this.props.queryState.current_query_id);
        }
    }

    componentDidUpdate() {
        if (this.props.queryState.state == searchStates.SEARCH_RUNNING) {
            // search status is running, request another status check
            this.props.actions.delayedCheckSearchStatus(this.props.queryState.current_query_id);
        }
    }
    
    /**
     * Report status as a table
     * @param {*} props properties
     */
     genStatusTable(props) {

        const {errorMsg, status, display_url} = props;

        // FIXME replace with CSS
        const displayUrlStyle={
            color: 'blue',
            fontWeight: 'bold',
            fontSize: 12,
        };

        // FIXME replace with CSS
        var statusStyle={
            fontWeight: 'bold',
            fontSize: 15,
        };

        return (
            <table id="StatusTable">
                <tbody>
                    <tr>
                        <td><text style={statusStyle}> {props.status} </text></td>
                        <td> <font color="red"> {props.errorMsg} </font></td>
                    </tr>
                    <tr>
                        <td> <text style={displayUrlStyle}> {display_url} </text> </td>
                    </tr>
                </tbody>
            </table>
        );
    }

    /**
     * Get the image and page counts from the global status
     */
    getImageAndPageStatus() {

        var message = noResultMessage;
        var downloadCount = 0;
        if (this.props.queryState.current_query_id != globals.DEFAULT_SEARCH_ID) {
            message = this.props.queryState.page_count + " pages and " + this.props.queryState.image_count 
                      + " images and " + downloadCount + " downloaded";
        }

        return message;
    }

    /**
     * Get the current search URL results
     */
    getSearchResultURL() {
        var display_url = undefined;
        if (this.props.queryState.current_query_id != globals.DEFAULT_SEARCH_ID) {
            display_url = this.props.queryState.current_url;
        }

        return display_url;
    }

    render() {
        const status = (this.props.queryState.errors ? this.props.queryState.errors : "");
        const pageImgStatus = this.getImageAndPageStatus();
        const display_url = this.getSearchResultURL();

        return(
            <div id="searchStatus">
                <this.genStatusTable
                    errorMsg = {status}
                    status = {pageImgStatus}
                    display_url = {display_url}
                />
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
export default connect(mapStateToProps, mapDispatchToProps)(StatusContainer);