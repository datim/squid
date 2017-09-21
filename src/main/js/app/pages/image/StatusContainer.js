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


var rp = require('request-promise');
const noResultMessage = "No Search Results";

/**
 * Report status as a table
 * @param {*} props 
 */
const StatusTable = props => {
    return (
        <table id="StatusTable">
            <tbody>
                <tr>
                    <th> {props.countStatus} Downloaded</th>
                    <th> {props.status} </th>
                    <th> <font color="red"> {props.errorMsg} </font></th>
                </tr>
            </tbody>
        </table>
    );
}

/**
 * Container for Search Status
 */
class StatusContainer extends Component {

    constructor(props) {
        super(props);
        this.state = {
            downloadCount: 0,
            imageCount: null,
            pageCount: null
        };
    }

    // get the initial status when page mounts if status is not -1
    componentDidMount() {
        if (this.props.queryState.searchState.state == searchStates.SEARCH_RUNNING) {
            // search status is running, request another status check
            this.props.actions.delayedCheckSearchStatus(this.props.queryState.server, this.props.queryState.port, this.props.queryState.searchState);
        }
    }

    componentDidUpdate() {
        if (this.props.queryState.searchState.state == searchStates.SEARCH_RUNNING) {
            // search status is running, request another status check
            this.props.actions.delayedCheckSearchStatus(this.props.queryState.server, this.props.queryState.port, this.props.queryState.searchState);
        }
    }

    /**
     * Get the image and page counts from the global status
     */
    getImageAndPageStatus() {

        var message = noResultMessage;
        if (this.props.queryState.searchState.current_query_id != globals.DEFAULT_SEARCH_ID) {
            message = this.props.queryState.searchState.page_count + " pages and " + this.props.queryState.searchState.image_count + " images";
        }

        return message;
    }

    render() {

        const status = (this.props.queryState.searchState.errors ? this.props.queryState.searchState.errors : "");
        const pageImgStatus = this.getImageAndPageStatus();

        return(
            <div id="searchStatus">
                <StatusTable
                    countStatus = {this.state.downloadCount}
                    errorMsg = {status}
                    status = {pageImgStatus}
                />
            </div>
        )
    }
}

// map state to properties
const mapStateToProps = (state, ownProps) => {
    return {
        queryState: state.queryState
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