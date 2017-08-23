'use-strict'
/**
 * State Actions
 */

/**
 * Action for clicking search button
 * @param {*} searchState 
 */
export const toggleSearchButton = text => {
    return {
        // identifier
        type: 'TOGGLE_SEARCH_BUTTON',
        text: text
    }
}