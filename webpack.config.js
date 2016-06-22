"use strict";

var debug = true;
var webpack = require('webpack');
var path = require('path');
var rootPath = path.join(__dirname, "src", "main", "resources", "public");

module.exports = {
  context: rootPath,
  entry: './js/initreact.js',
  //devtool: debug ? "inline-sourcemap" : null,
  devtool: debug ? "eval-source-map" : null,
  output: {
    path: rootPath,
    filename: 'initreact.min.js',
    sourceMapFileName: 'initreact.min.js.map'
  },

  module: {
    loaders: [
      {
        test: /\.js?$/,
        exclude: /(node_modules|bower_components)/,
        loader: 'babel-loader',
        query: {
          presets: ['react', 'es2015', 'stage-0'],
          plugins: ['transform-class-properties', 'transform-decorators-legacy'],
        }
      }
    ]
  },

  plugins: debug ? [] : [
    new webpack.optimize.DedupePlugin(),
    new webpack.optimize.OccurenceOrderPlugin(),
    new webpack.optimize.UglifyJsPlugin({ mangle: false, sourcemap: false }),
  ],
};
