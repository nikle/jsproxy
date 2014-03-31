Function.prototype.clone = function() {
    var fct = this;
    var clone = function() {
	fct.__closure__ = arguments.callee.__closure__;
        return fct.apply(this, arguments);
    };
    clone.prototype = fct.prototype;
    for (property in fct) {
        if (fct.hasOwnProperty(property) && property !== 'prototype') {
            clone[property] = fct[property];
        }
    }
    return clone;
};