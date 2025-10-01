var argscheck = require('cordova/argscheck'),
    exec = require('cordova/exec');

/**
 * This represents the insets (safe area) of the device.
 * @constructor
 */
function InsetsPlugin() {
    this.top = 0;
    this.bottom = 0;
    this.left = 0;
    this.right = 0;
}

/**
 * Get window insets (safe area) from the native side.
 *
 * @param {Function} successCallback The function to call when the insets data is available
 * @param {Function} errorCallback The function to call when there is an error getting the insets data. (OPTIONAL)
 */
InsetsPlugin.prototype.getInsets = function(successCallback, errorCallback) {
    argscheck.checkArgs('fF', 'InsetsPlugin.getInsets', arguments);
    exec(successCallback, errorCallback, "InsetsPlugin", "getInsets", []);
};

module.exports = new InsetsPlugin();
