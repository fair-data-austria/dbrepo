module.exports = () => {
  return {
    require: [
      './test/_setup.js'
    ],
    timeout: '20s',
    ignoredByWatcher: ['!**/*.{js,vue}'],
    verbose: true,
    color: true,
    babel: true
  }
}
