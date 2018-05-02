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
 * Container for displaying image results
 */
class ImageResultContainer extends Component {

    constructor(props) {
        super(props);
        this.state = {};
    }

    /** Generate a table cell that holds an image **/
    genImageCell(image, defaultWidth, defaultHeight) {

        const imageWidth = (image.width < defaultWidth) ? image.width : defaultWidth;

        return (
            <td id='imageTableCell'
                width={defaultWidth}
                key={image.id}>
                <img className="img-responsive" src={image.url} alt={image.id} width={imageWidth}/>
            </td>);
    }

    /** Generate a row of ImageCells for the table */
    genImageRow(createImageCell, imageRowList, defaultWidth, defaultHeight) {
        // define the columns of images for each row
        const tableColumns = imageRowList.map(rowImage => {
            return (createImageCell(rowImage, defaultWidth, defaultHeight));
        });

        // return the full table row
        return(
            <tr id='imageTableRow'
                key={imageRowList[0].id}>
                {tableColumns}
            </tr>
        )
    }

    /** Generate a  table that displays images **/
    genImageTable(props) {

        const {images, numRows, numColumns, defaultWidth, defaultHeight, createImageCell, createImageRow} = props;

        // convert to a multi-array based on row count
        const imageMultiMap = createMultiArray(images, numRows, numColumns);
        
        const tableRows = imageMultiMap.map(rowList => {
            return(createImageRow(createImageCell, rowList, defaultWidth, defaultHeight));
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
     * Calculate the number of rows that should be generated. If the number of rows is not a whole number, round to the next whole number
     */
    getRowCount() {
        const rowSize = Math.ceil(this.props.queryResults.images.length / globals.DEFAULT_IMAGE_RESULT_COLUMNS);
        return rowSize;
    }

    render() {
        const rowCount = this.getRowCount();

        return(
            <div id="ImageResults">
                <this.genImageTable numRows={rowCount} 
                            images={this.props.queryResults.images}
                            numColumns={globals.DEFAULT_IMAGE_RESULT_COLUMNS}
                            defaultHeight={globals.IMAGE_THUMBNAIL_HEIGHT}
                            defaultWidth={globals.IMAGE_THUMBNAIL_WIDTH}
                            createImageCell={this.genImageCell}
                            createImageRow={this.genImageRow} />
            </div>
        )
    }
};

// map state to properties
const mapStateToProps = (state, ownProps) => {
    return {
        queryResults: state.queryState.searchResults
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