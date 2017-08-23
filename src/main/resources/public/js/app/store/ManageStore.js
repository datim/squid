'use-strict'

import { createStore } from 'redux'
import rootReducer from "../reducers/MainReducer"

let store;

// Initialize the store
export function initStore (initialState) {
    store = createStore(rootReducer);
    return store;
}

// return the store
export function getStore () { 
    return store; 
}
