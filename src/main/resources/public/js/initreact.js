import React from "react";
import ReactDOM from "react-dom";

class PhotoGrid extends React.Component {

  // render the output
  render() {
    var { myphotoSrc } = this.props;

    return(
      <div>
        <br />
        <img src={myphotoSrc}></img>
        <h3> Placeholder </h3>
        <br />
      </div>
    )
  }
}

const app = document.getElementById('ReactApp');
const myPhotoSource = "http://cdn2.hubspot.net/hub/451063/hubfs/mobile_uploads/react_2.png?t=1458853862314&width=50&height=50"

ReactDOM.render(<PhotoGrid myphotoSrc={myPhotoSource} />, app);
