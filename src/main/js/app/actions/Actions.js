'use-strict'
/**
 * State Actions
 */
import * as actions from './ActionTypes';
import * as searchStates from './SearchStates';
import * as globals from '../common/GlobalConstants';
import { getServerURL } from '../common/Utils';

const rp = require('request-promise');

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
 * Helper function to fetch results of a URL and provide a JSON formatted object
 * @param {*} providedURL The URL to make a GET request to
 */
const urlGetRequest = (providedURL) => {
    return rp(providedURL).then( result => {
        return ((result) ? JSON.parse(result) : result);
    }).catch( err => {
        console.log("Unable to retrieve and parse data from url: '" + providedURL + "'. Error: " + err);
    });
}

/**
 * Request a search start or stop through a user action
 * Dispatch provided by Thunk
 * @param {*} queryURL - The url to request
 * @param {*} currentQueryId - current search id
 */
export function toggleSearch(queryURL, searchState, currentQueryId) {

    return (dispatch) => {

        // construct the query URL
        var searchURI = getServerURL() + globals.SEARCH_ROOT + '/start';
        var dispatchRequest = actions.REQUEST_QUERY_START;
        var options = NaN;

        if (searchState == searchStates.SEARCH_RUNNING) {

            // stop a current search
            searchURI = getServerURL() + globals.SEARCH_ROOT + '/' + currentQueryId + '/stop';
            console.log('stopping current search. URL: ', searchURI);            
            dispatchRequest = actions.REQUEST_QUERY_STOPPED;

            options = {
                method: 'POST',
                uri: searchURI,
                body: {},
                json: true // Automatically stringifies the body to JSON
            };

        } else if (searchState == searchStates.SEARCH_STOPPED) {

            // Start a new search
            console.log('starting search on URL: ', searchURI);
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
 * @param {*} current_query_id - the current query id to check
 */
export function delayedCheckSearchStatus(current_query_id) {
    
    const statusURL = getServerURL() + globals.QUERY_ROOT + '/' + current_query_id + '/status';
    const resultsURL = getServerURL() +  globals.QUERY_ROOT + '/' + current_query_id + '/image?page=0' + '&size=' + globals.DEFAULT_QUERY_PAGE_SIZE;
    
    return (dispatch) => {

        // set timer for CHECK_STATUS_INTERVAL_MS, then fetch query status and alter global state
        new Promise( (resolve) => {
            setTimeout( () => {
                console.log("Timer for " + globals.CHECK_STATUS_INTERVAL_MS + " expired. Checking status");
                resolve();
            }, globals.CHECK_STATUS_INTERVAL_MS); 

        }).then( () => {
            // fetch status and image results
            return Promise.all([urlGetRequest(statusURL), urlGetRequest(resultsURL)]).then((requestResults) => {

                // images may not be returned for API
                const statusResults = requestResults[0];

                // if results are not an array, replace with an array
                const imageResults = ((requestResults[1]) ? requestResults[1].content : []);

                console.log("Status is: " + JSON.stringify(statusResults) + ". Images found: " + imageResults.length);
    
                // check if the query has s topped
                const currentActionType = ((statusResults.status === "STOPPED") ? actions.QUERY_STATUS_STOPPED : actions.QUERY_STATUS_RUNNING);
    
                // dispatch results to change state
                dispatch({ type: currentActionType, imageCount: statusResults.imageCount, pageCount: statusResults.pageCount, images: imageResults});
            });
        }).catch( err => {
            console.log("Unable to fetch query status for id " + current_query_id + ". Error: " + err);       
        });
    }
}