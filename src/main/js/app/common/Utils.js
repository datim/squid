'use-strict'

import * as globals from "./GlobalConstants";
const rp = require('request-promise');

/**
 * Convert a single list of items into a multi-dimensional 
 * array 
 * FIXME - move to helper function
 * @param {*} imageList 
 * @param {*} numRows number of rows in the multi-dimensional array
 * @param {*} numRowEntries number of entries per row
 */
export const createMultiArray = (itemList, numRows, numRowEntries) => {

    var itemCount=0;
    var multiArray = [];

    for(var i=0; i < numRows; i++) {
        var rowList = [];

        for (var j=0; j < numRowEntries; j++) {

            if (itemCount < itemList.length) {
                rowList.push(itemList[itemCount++]);
            }
        }

        multiArray.push(rowList);
    }
    return multiArray;
}

/**
 * Generate the base URL to the server
 */
export const getServerURL = () => {
    return('http://' + globals.HOST + ':' + globals.PORT);
}

/**
 * Helper function to fetch results of a URL and provide a JSON formatted object
 * @param {*} providedURL The URL to make a GET request to
 */
export const urlGetRequest = (providedURL) => {
    return rp(providedURL).then( result => {
        return ((result) ? JSON.parse(result) : result);
    }).catch( err => {
        console.log("Unable to retrieve and parse data from url: '" + providedURL + "'. Error: " + err);
    });
}
