"use strict";
// Development configuration for webpack
// For webpack source maps, see: https://webpack.github.io/docs/configuration.html
// To execute:
//    # webpack --config webpack.config.dev.js

var webpack = require('webpack');
var path = require('path');
var rootPath = path.join(__dirname, "src", "main", "resources", "public");

module.exports = {
  context: rootPath,
  devtool:  "eval-source-map",
  entry: {
  	app: './js/Launch.js',
  },
  output: {
  	filename: 'index.js',
    path: path.resolve(__dirname, 'src', 'main', 'resources', 'public')
  },
  //Babel compiler
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
  node: {
    // reference: http://webpack.github.io/docs/configuration.html#node
    console: false,
    fs: 'empty',
    net: 'empty',
    tls: 'empty'
  },
};
