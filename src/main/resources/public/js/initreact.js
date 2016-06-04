import React from "react";
import ReactDOM from "react-dom";

import SquidVersion from "./components/SquidVersion"
import PhotoPanel from "./components/PhotoPanel"
import IconPanel from "./components/IconPanel"

class MyApp extends React.Component {

  constructor() {
    super();
    this.photosPerRow = 4;
  }

  render() {

    return(
      <div>
        <br />
        <IconPanel/>
        <br />
        <SquidVersion/>
        <PhotoPanel photosPerRow={this.photosPerRow} />
        <br />
      </div>
    )
  }
}

// render the main app
const app = document.getElementById('ReactApp');
ReactDOM.render(<MyApp/>, app);
