'use-strict'
/**
 * State Actions
 */
import * as actions from '../actions/ActionTypes';
import * as searchStates from '../actions/SearchStates';

var rp = require('request-promise');
const searchRoot = "/crawl/search";


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
 * Request a search start or stop
 * Dispatch provided by Thunk
 * @param {*} host  the host IP to send the POST request to
 * @param {*} port - the port to send POST request to 
 * @param {*} queryURL - The url to request
 */
export function toggleSearch(host, port, queryURL, searchState) {

    return (dispatch) => {

        // construct the query URL
        var searchURI = "http://" + host + ":" + port + searchRoot + "/search";
        var dispatchRequest = actions.REQUEST_QUERY_START;
        var options = NaN;

        if (searchState == searchStates.SEARCH_RUNNING) {

            // stop a current search
            console.log("stopping current search");
            searchURI = "http://" + host + ":" + port + searchRoot + "/stop";
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
    