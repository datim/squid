import React from "react";
import ReactDOM from "react-dom";

import SquidVersion from "./components/SquidVersion"
import Statistics from "./components/Statistics"
import PhotoPanel from "./components/PhotoPanel"

class IconDisplay extends React.Component {

  constructor() {
    super();
    this.photosPerRow = 6;
  }

  render() {
    var { myphotoSrc } = this.props;

    return (
      <div>
        <br />
        <img src={myphotoSrc}></img>
        <br />
      </div>

    )
  }
}

class MyApp extends React.Component {

  constructor() {
    super();
    this.photoSource = 'http://cdn2.hubspot.net/hub/451063/hubfs/mobile_uploads/react_2.png?t=1458853862314&width=50&height=50';
    this.photosPerRow = 4;
  }

  render() {

    return(
      <div>
        <br />
        <IconDisplay myphotoSrc={this.photoSource}/>
        <SquidVersion/>
        <Statistics />
        <PhotoPanel photosPerRow={this.photosPerRow} />
        <br />
      </div>
    )
  }
}

// render the main app
const app = document.getElementById('ReactApp');
ReactDOM.render(<MyApp/>, app);
