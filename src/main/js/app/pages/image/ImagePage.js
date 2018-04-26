'use-strict'
import React, {Component} from 'react';

import SideNavBarPages from "../../common/nav/SideNavBar"
import SearchContainer from "./SearchContainer"
import StatusContainer from "./StatusContainer"
import ImageResultContainer from "./ImageResultContainer"


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
        <StatusContainer/>
        <ImageResultContainer/>
      </div>
    )
  }

}
