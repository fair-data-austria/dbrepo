module.exports = {
  sourceMaps: 'both',
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
