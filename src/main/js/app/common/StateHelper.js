'use-strict'

/**
 * Helper function for determining current state
 */
import * as searchStates from "../actions/SearchStates";

// return true if currents state is stopped
export const isStateStopped = currentState => {
    return (currentState == searchStates.SEARCH_STOPPED);
}

// return true if current state is running
export const isStateRunning = currentState => {
    return (currentState == searchStates.SEARCH_RUNNING);
}