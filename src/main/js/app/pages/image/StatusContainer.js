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
 * Container for Search Inputs
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

    // fetch the status about the current query
    fetchStatus() {
        
        const statusAPI = "http://" + this.props.queryState.server + ":" + this.props.queryState.port 
                                    + globals.QUERY_ROOT + "/" + this.props.queryState.searchState.current_query_id + "/status";
        
        return rp(statusAPI)
            .then(results => {

                var resultsJSON = JSON.parse(results);

                // update state
                console.log("Status: Image Count " + resultsJSON.imageCount + ", Page Count: " + resultsJSON.nodeCount);
                this.setState({
                    imageCount: resultsJSON.imageCount,
                    pageCount: resultsJSON.nodeCount
                })
            })
            .catch(err => {
                // do not update state
                console.log("Error fetching status results from " + statusAPI + ". Error: " + err);
            });
    }

    // get the initial status when page mounts if status is not -1
    componentDidMount() {
        if (this.props.queryState.searchState.current_query_id != globals.DEFAULT_SEARCH_ID) {
            this.fetchStatus()
        } else {
            console.log("No Search Performed. Will not fetch Search Status")
        }
    }

    componentDidUpdate() {
        //this.delayThenCheckStatus()   // FIXME ADD
    }

    // check the status of the container    
    /**
     * check the status of a container
     * FIXME - add this when stop mechanism works
     */
    delayThenCheckStatus() {

        // if search is started, then periodically request status updates
        if (isStateRunning(this.props.queryState.searchState.state)) {
            var timerCheck = new Promise( (resolve) => {
                setTimeout( () => {
                    console.log("Checking Status");
                    resolve();
                }, 5000);
            })
    
            // invoke the timer and call fetch status when done
            timerCheck.then(this.fetchStatus());
        }
    }

    // generate the page and image count status message
    getImageAndPageStatus() {

        var message = noResultMessage;

        if (this.state.imageCount!= null && this.state.pageCount != null) {
            message = this.state.pageCount + " pages and " + this.state.imageCount + " images"; 
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