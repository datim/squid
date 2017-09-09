/** Initial redux state */
import * as states from './actions/SearchStates';

// define the initial state for the store
export default {
    queryState: {
        server : 'localhost',
        port: 8080,
        searchState : {
            state : states.SEARCH_STOPPED,
            current_query_id : -1,
            current_url : null,
            errors : null
        }
    } 
};
