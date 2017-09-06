'use-strict'

import { createStore, applyMiddleware } from 'redux'
import createLogger from 'redux-logger';
import thunkMiddleware from 'redux-thunk';
import rootReducer from "../reducers/MainReducer"

let store;

// Initialize the store
export function initStore (initialState) {
    store = createStore(rootReducer, initialState, applyMiddleware(thunkMiddleware));
    return store;
}

// return the store
export function getStore () { 
    return store; 
}