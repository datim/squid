'use-strict'
import { combineReducers } from 'redux'
import searchState from './SearchStateReducer'

// combine all redux reducers into a single reducer
// reducers are mapped to the states by key/value pairs
const rootReducer = combineReducers(
  { queryState: searchState }
)

export default rootReducer;