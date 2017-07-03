'use-strict'
import React, {Component} from 'react';

import SideNavBarPages from "../../common/nav/SideNavBar"

export default class QueryPage extends Component {

  constructor(props) {
    super(props);
  }

  render() {
    return(
      <div>
        <h1>Query Details</h1>
        <SideNavBarPages/>
      </div>
    )
  }
}
