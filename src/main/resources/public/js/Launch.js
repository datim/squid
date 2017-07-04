/** Main Application **/

import React, {Component} from 'react';
import { render } from 'react-dom';
import { Router, Route, browserHistory, hashHistory} from 'react-router'

import ImagePage from "./app/pages/image/ImagePage"
import QueryPage from "./app/pages/query/QueryPage"
import AdminPage from "./app/pages/admin/AdminPage"

// https://medium.com/@pshrmn/a-simple-react-router-v4-tutorial-7f23ff27adf
//https://github.com/reactjs/react-router-tutorial/tree/master/lessons/03-navigating-with-link

/**
 * Define routes to each of the defined pages in the UI
 *
 **/
render((
  <Router history={hashHistory}>
    <Route path="/" component={ImagePage}/>
    <Route path="/query" component={QueryPage}/>
    <Route path="/admin" component={AdminPage}/>
  </Router>
), document.getElementById('ReactApp'))
