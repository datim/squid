'use-strict'
import * as types from '../actions/ActionTypes';
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
const searchStateReducer = (state=[], action) => {

  switch(action.type) {

    case 'CLICK_SEARCH_BUTTON':
      var returnType = NaN;
      if (state.search_status == types.SEARCH_STOPPED) {
        console.log("Starting Search")
        returnType = types.SEARCH_STARTING;
      }

      else if (state.search_status == types.SEARCH_STARTING) {
        console.log(" Search Running")
        returnType = types.SEARCH_RUNNING;
      }

      else if (state.search_status == types.SEARCH_RUNNING) {
        console.log("Stopping Search")
        returnType = types.SEARCH_STOPPING;
      }

      else if (state.search_status == types.SEARCH_STOPPING) {
        console.log("Search Stopped")
        returnType = types.SEARCH_STOPED;
      }

      // construct state with new status
      return [
        ...state,
        {
          'search_status': returnType
        }
      ]

    default:
      return state;
  }
};

export default searchStateReducer
