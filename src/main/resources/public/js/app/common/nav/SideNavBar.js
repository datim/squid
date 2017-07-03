import React, {Component} from 'react';
import { Link } from 'react-router'

/**
 * Side Nav Bar navigation for pages. Use Route Link to map back to pages defined by these routes
**/
export default class SideNavBarPages extends Component {
  render() {
    return(
      <div id="header">
      <table>
        <tr>
          <th><Link to="/">Images</Link></th>
          <th><Link to="/query">Query Details</Link></th>
          <th><Link to="/admin">Admin</Link></th>
        </tr>
      </table>
      </div>
    )
  }
}
