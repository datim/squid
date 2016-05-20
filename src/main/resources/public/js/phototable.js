  /*
   * React class
   *
   */
   class TableGraph extends React.Component {

     // construtor
     constructor(props) {
       super(props);
       this.start = 0;
     }

     // show the results
     render() {

       var { myphotoSrc } = this.props;
       return(
         <div>
            <br />
            <img src={myphotoSrc}></img>
         </div>
       );
     }
   }

   // render react object
   ReactDOM.render(<TableGraph myphotoSrc="http://cdn2.hubspot.net/hub/451063/hubfs/mobile_uploads/react_2.png?t=1458853862314&width=50&height=50" />, document.getElementById('PhotoResultsDiv'));
