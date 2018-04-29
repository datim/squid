/** Initial redux state */
import * as states from './actions/SearchStates';

// define the initial state for the store
export default {
    queryState: {
        searchState : {
            state : states.SEARCH_STOPPED,
            current_query_id : -1,
            current_url : null,
            image_count : 0,
            page_count : 0,
            errors : null
        },
        searchResults: {
            images: []            
        }
    }
};
