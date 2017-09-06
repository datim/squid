'use-strict'

/**
 * Helper function for determining current state
 */
import * as searchStates from "../actions/SearchStates";

// return true if currents state is stopped
export const isStateStopped = currentState => {
    return (currentState == searchStates.SEARCH_STOPPED);
}