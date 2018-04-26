'use-strict'
/**
 * Search Image results
 */
import React, {Component} from 'react';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';

import * as searchActions from "../../actions/Actions";
import * as globals from "../../common/GlobalConstants";
import { createMultiArray } from "../../common/Utils";



/**
 * Generate an image result table
 * @param {*} props 
 */
const ImageTable = (props) => {

    // convert to a multi-array based on row count
    const imageMultiMap = createMultiArray(props.images, props.numRows, props.numColumns);

    console.log('multiarray:', imageMultiMap);
    
    const tableRows = imageMultiMap.map( rowList => {

        // define the columns of images for each row
        const tableColumns = rowList.map(rowImage => {
            // if the default image size is smaller than the image, take the default
            const imageWidth = (rowImage.width < props.width) ? rowImage.width : props.width;
            return (<td id='imageResultColumn'
                        width={props.width}
                        key={rowImage.id}>
                        <img className="img-responsive" src={rowImage.url} alt={rowImage.id} width={imageWidth}/>
                    </td>);
        });

        // return the full table row
        return(
            <tr id='imageResultRow'
                key={rowList[0].id}>
                {tableColumns}
            </tr>
        )
    });

    return (
        <table>
            <tbody>
                {tableRows}
            </tbody>
        </table>
    );
}

/**
 * Container for displaying image results
 */
class ImageResultContainer extends Component {

    constructor(props) {
        super(props);
        this.state = {};
    }

    /**
     * Calculate the number of rows that should be generated. If the number of rows is not a whole number, round to the next whole number
     */
    getRowCount() {
        const rowSize = Math.ceil(this.props.queryState.searchState.image_count / globals.DEFAULT_IMAGE_RESULT_COLUMNS);
        console.log('Row size is ' + rowSize);
        return rowSize;
    }

    // FIXME - dynamically add columns
    render() {
        console.log("Images found: ", this.props.queryState.searchState.images);
        const rowCount = this.getRowCount();
        const imageList = this.props.queryState.searchState.images;

        return(
            <div id="ImageResults">
                <ImageTable numRows={rowCount} 
                            images={imageList}
                            numColumns={globals.DEFAULT_IMAGE_RESULT_COLUMNS}
                            height={globals.IMAGE_THUMBNAIL_HEIGHT}
                            width={globals.IMAGE_THUMBNAIL_WIDTH} />
            </div>
        )
    }
};

// map state to properties
const mapStateToProps = (state, ownProps) => {
    return {
        queryState: state.queryState
    }
  };
  
// map all actions to properties
const mapDispatchToProps = (dispatch) => {
    return {
      actions: bindActionCreators(searchActions, dispatch)
    }
}
  
// connect state to class
export default connect(mapStateToProps, mapDispatchToProps)(ImageResultContainer);