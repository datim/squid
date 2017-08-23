/**
 * Main Application
 *
 * References:
 *    https://medium.com/@pshrmn/a-simple-react-router-v4-tutorial-7f23ff27adf
 *    https://github.com/reactjs/react-router-tutorial/tree/master/lessons/03-navigating-with-link
**/

import React, {Component} from 'react';
import { render } from 'react-dom';
import { Provider } from 'react-redux'
import { Router, Route, browserHistory, hashHistory} from 'react-router'
import ImagePage from "./app/pages/image/ImagePage"
import QueryPage from "./app/pages/query/QueryPage"
import AdminPage from "./app/pages/admin/AdminPage"

import { initStore } from "./app/store/ManageStore"
import initialState from "./app/initialState"

// Create redux store
const store = initStore(initialState)

render((
  <Provider store={store}>
    <Router history={hashHistory}>
        <Route path="/" component={ImagePage}/>
      <Route path="/query" component={QueryPage}/>
      <Route path="/admin" component={AdminPage}/>
    </Router>
  </Provider>
), document.getElementById('ReactApp'));