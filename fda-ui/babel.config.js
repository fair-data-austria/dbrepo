module.exports = {
  sourceMaps: 'both',

  // for jest / e2e
  presets: [['@babel/preset-env', { targets: { node: 'current' } }]],

  env: {
    test: {
      plugins: [
        '@babel/plugin-transform-runtime',
        [
          'module-resolver',
          {
            root: ['.'],
            alias: {
              '@': '.',
              '~': '.'
            }
          }
        ]
      ]
    }
  }
}
