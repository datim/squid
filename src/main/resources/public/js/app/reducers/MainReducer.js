'use-strict'
import { combineReducers } from 'redux'
import sampleReducer from './SampleReducer'
import searchStateReducer from './SearchStateReducer'

// combine all redux reducers into a single reducer
const rootReducer = combineReducers(
  { sampleReducer, searchStateReducer }
)

export default rootReducer;