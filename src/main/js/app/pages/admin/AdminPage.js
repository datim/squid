'use-strict'
import React, {Component} from 'react';

import SideNavBarPages from "../../common/nav/SideNavBar"

export default class AdminPage extends Component {

  constructor(props) {
    super(props);
  }

  render() {
    return(
      <div>
        <h1>Admin</h1>
        <SideNavBarPages/>
      </div>
    )
  }
}
