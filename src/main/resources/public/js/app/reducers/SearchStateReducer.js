'use-strict'
import * as actions from '../actions/ActionTypes';
import * as searchStates from '../actions/SearchStates';

var rp = require('request-promise');
const crawlAPI = "/crawl/search";

/**
 * Handle the changing search state
 **/

/**
 * The state contains the following components:
 *          - status: 'STOPPED', 'STARTED'
 *          - changed (boolean): 0 if state is changing
 *
 * The action is assumed to be: START, STOP
 **/
const searchState = (state={}, action) => {

  var newState = state;
  
  switch(action.type) {

    case actions.REQUEST_QUERY_STARTED:

      if (state.searchState.state == searchStates.SEARCH_STOPPED) {
        // Set state to start for the current query. Clear any errors, record the ID and the search URL
        return {
          ...state,
          searchState : {
            state: searchStates.SEARCH_RUNNING,
            current_url : action.searchURI,
            current_query_id : action.id,
            errors: NaN
          }
        }
      } else {
        return state;
      }

    case actions.REQUEST_QUERY_STOPPED:
      if (state.searchState.state == searchStates.SEARCH_RUNNING) {      
        // Set state to stop for the current query. Set state to stopped, clear any errors  
        return {
          ...state,
          searchState : {
            state: searchStates.SEARCH_STOPPED,
            errors: NaN          
          }
        }

      } else {
        return state;
      }

    case actions.REQUEST_QUERY_FAILED:
      // Failed to invoke or start or stop search. Set state to stopped, record error and search URL
      return {
        ...state,
        searchState : {
          state: searchStates.SEARCH_STOPPED,          
          current_url : action.searchURI,
          errors: action.error          
        }
      }

    default:
    return state;
  }
};

export default searchState
