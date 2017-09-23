'use-strict'
/**
 * State Actions
 */
import * as actions from './ActionTypes';
import * as searchStates from './SearchStates';
import * as globals from "../common/GlobalConstants";

var rp = require('request-promise');

/**
 * Action for clicking search button
 * @param {*} searchState 
 */
export const toggleSearchButton = (searchInput, searchButtonOn) => {
    return {
        // identifier
        type: 'TOGGLE_SEARCH_BUTTON',
        searchInput: searchInput,
        searchButtonOn: searchButtonOn
    }
}

/**
 * Request a search start or stop through a user action
 * Dispatch provided by Thunk
 * @param {*} host  the host IP to send the POST request to
 * @param {*} port - the port to send POST request to 
 * @param {*} queryURL - The url to request
 * @param {*} currentQueryId - current search id
 */
export function toggleSearch(host, port, queryURL, searchState, currentQueryId) {

    return (dispatch) => {

        // construct the query URL
        var searchURI = "http://" + host + ":" + port + globals.SEARCH_ROOT + "/start";
        var dispatchRequest = actions.REQUEST_QUERY_START;
        var options = NaN;

        if (searchState == searchStates.SEARCH_RUNNING) {

            // stop a current search
            searchURI = "http://" + host + ":" + port + globals.SEARCH_ROOT + "/" + currentQueryId + "/stop";
            console.log("stopping current search. URL: " + searchURI);            
            dispatchRequest = actions.REQUEST_QUERY_STOPPED;

            options = {
                method: 'POST',
                uri: searchURI,
                body: {},
                json: true // Automatically stringifies the body to JSON
            };

        } else if (searchState == searchStates.SEARCH_STOPPED) {

            // Start a new search
            console.log("starting current search");
            dispatchRequest = actions.REQUEST_QUERY_STARTED;

            options = {
                method: 'POST',
                uri: searchURI,
                body: {
                    url: queryURL
                },
                json: true // Automatically stringifies the body to JSON
            };

        } else {
            // nothing to do here
            console.log("Unable to proceed with toggleSearch. Unexpected search state: " + searchState)
            return;
        }

        console.log("Posting URL '" + searchURI + "'");

        // Post request
        rp(options)
            .then( resultBody => {
                // dispatch results to reducer to change state
                dispatch( {type: dispatchRequest, id: resultBody, searchURI: searchURI })
            })
            .catch( err => {
                // unable to invoke query, report error
                console.log("Unable to make POST request to '" + searchURI + "'. Error: " + err);                
                dispatch ({ type: actions.REQUEST_QUERY_FAILED, dispatchType: dispatchRequest, searchURI: searchURI, error: err })
            });
    }
};
    
/**
 * Check the search status every 5 seconds until search stops
 * @param {*} host - the backend server to query
 * @param {*} port  - the backend server port to query
 * @param {*} searchState - the current state of the system
 */
export function delayedCheckSearchStatus(host, port, searchState) {

    return (dispatch) => {
        var statusURL = "http://" + host + ":" + port + "/" + globals.QUERY_ROOT + "/" + searchState.current_query_id + "/status";

        // set timer for CHECK_STATUS_INTERVAL_MS, then fetch query status and alter global state
        new Promise((resolve) => {
            setTimeout( () => {
                console.log("Timer for " + globals.CHECK_STATUS_INTERVAL_MS + " expired. Checking status");
                resolve();
            }, globals.CHECK_STATUS_INTERVAL_MS);
            
        }).then( () => {
            return rp(statusURL).then( resultBody => {
                var statusResults = JSON.parse(resultBody);

                if (statusResults.status == "STOPPED") {
                    // query has stopped. Fire stop state change
                    dispatch({ type: actions.REQUEST_QUERY_STOPPED, imageCount: statusResults.imageCount, pageCount: statusResults.pageCount});

                } else {
                    // query still running. Fire continue running state change
                    dispatch({ type: actions.QUERY_STATUS_RUNNING, imageCount: statusResults.imageCount, pageCount: statusResults.pageCount});
                }
            });
        }).catch( err => {
            console.log("Unable to fetch query status for id " + searchState.current_query_id + ". Error: " + err);            
        });
    }
}