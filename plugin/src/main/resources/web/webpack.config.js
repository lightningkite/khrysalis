const path = require('path');
// const BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin;

module.exports = {
  // plugins: [
  //   new BundleAnalyzerPlugin()
  // ],
  entry: './src/index.ts',
  devtool: 'inline-source-map',
  module: {
    rules: [
      // all files with a `.ts` or `.tsx` extension will be handled by `ts-loader`
      { test: /\.tsx?$/, loader: "ts-loader" },
      {
        test: /\.s[ac]ss$/i,
        use: [
          // Creates `style` nodes from JS strings
          'style-loader',
          // Translates CSS into CommonJS
          'css-loader',
          // Compiles Sass to CSS
          'sass-loader',
          // Fixes URLS
          'resolve-url-loader'
        ],
      },
      {
        test: /\.(png|jpe?g|gif)$/i,
        use: [
          {
            loader: 'file-loader',
          },
        ],
      },
      {
        test: /\.html$/i,
        loader: 'html-loader',
        options: {
          // Disables attributes processing
          attributes: false,
        },
      },
    ],
  },
  resolve: {
    extensions: ['.ts', '.js', '.html', '.scss']
  },
  output: {
    filename: 'index.js',
    path: path.resolve(__dirname, 'dist'),
  },
  devServer: {
    contentBase: './dist',
  }
};
