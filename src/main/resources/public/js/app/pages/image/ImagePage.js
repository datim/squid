'use-strict'
import React, {Component} from 'react';

import SideNavBarPages from "../../common/nav/SideNavBar"
import SearchContainer from "./SearchContainer"

/**
 * Represent the Image Search Page
 */
export default class ImagePage extends Component {

  constructor(props) {
    super(props);
  }

  render() {

    return(
      <div>
        <h1>Image</h1>
        <SideNavBarPages/>
        <SearchContainer/>
      </div>
    )
  }
}
