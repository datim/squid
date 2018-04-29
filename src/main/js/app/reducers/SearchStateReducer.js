'use-strict'
import * as actions from '../actions/ActionTypes';
import * as searchStates from '../actions/SearchStates';

/**
 * Reducer to alter the search state. Triggers a global state change on all components
 **/
const searchState = (state={}, action) => {

  var newState = state;

  console.log('received action ', action.type);
  
  switch(action.type) {

    case actions.REQUEST_QUERY_STARTED:

      if (state.searchState.state == searchStates.SEARCH_STOPPED) {

        // Start a new search
        // Set state to start for the current query. Clear any errors, record the ID and the search URL, and clear counts
        return {
          ...state,
          searchState : {
            state: searchStates.SEARCH_RUNNING,
            current_url : action.searchURI,
            current_query_id : action.id,
            page_count : 0,          
            image_count : 0,
            errors: null
          },
          searchResults : {
            images : []
          }
        }
      } else {
        return state;
      }

    case actions.QUERY_STATUS_STOPPED: 
      // The query has stopped from the back end.  Update the latest status for page and image count. Set state to stopped, clear any errors  
      return {
        ...state,
        searchState : {
          state: searchStates.SEARCH_STOPPED,
          current_query_id : state.searchState.current_query_id,            
          image_count : action.imageCount,
          page_count : action.pageCount,
          current_url : action.searchURI,
          errors: null
        }, 
        searchResults : {
          images : action.images
        }
      }

    case actions.REQUEST_QUERY_STOPPED:
      if (state.searchState.state == searchStates.SEARCH_RUNNING) { 
        // The user has requested a stop the query.     Set state to stopped, clear any errors  
        return {
          ...state,
          searchState : {
            state: searchStates.SEARCH_STOPPED,
            current_query_id : state.searchState.current_query_id,            
            current_url : state.searchState.searchURI,            
            page_count : state.searchState.page_count,          
            image_count : state.searchState.image_count,
            errors: null
          },
          searchResults : {
            images : state.searchState.images
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
          state : searchStates.SEARCH_STOPPED,
          current_query_id : state.searchState.current_query_id,
          current_url : action.searchURI,
          page_count : state.searchState.page_count,          
          image_count : state.searchState.image_count,
          errors : action.error.message
        },
        searchResults : {
          images : state.searchState.images          
        }
      }

    case actions.QUERY_STATUS_RUNNING:
      // status returned that query status is still running
      return {
        ...state, 
        searchState : {        
          state : searchStates.SEARCH_RUNNING,
          current_query_id : state.searchState.current_query_id,
          image_count : action.imageCount,
          page_count : action.pageCount,
          current_url : action.searchURI,
          errors : null
        },
        searchResults : {
          images : action.images
        }
      }

    default:
    return state;
  }
};

export default searchState
