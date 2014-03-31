window.___ids = 0;
window.___fns = [];
window.___samples = {};
window.___objs = [];
window.___fun = {};
function ___isNumber (o) {
	  return ! isNaN (o-0);
}
function ___pushFnName(name) {
	window.___fns.push(name);
}
function ___popFnName() {
	window.___fns.pop();
}
function ___profileSample(time) {
	var name = window.___fns[window.___fns.length - 1];
	var current = window.___samples[name];
	if(current) {
		window.___samples[name] = current + time
	} else {
		window.___samples[name] = time;
	}
}
function ___getter(obj, name) {
	if((obj === undefined) || (obj === null) || (typeof(obj) !== 'object')) {
		return obj[name];
	}
	if(obj.___id === undefined) {
		obj.___id = window.___ids++;
		window.___objs.push(obj);
		obj.___read = {};
		obj.___write = {};
	}
	var fnName;
	if(window.___fns.length === 0) {
		fnName = "(top)";
	} else {
		fnName = window.___fns[window.___fns.length - 1];
	}
	obj.___read[fnName] = true;
	var list = ___fun[fnName];
	if(!list) {
		list = {};
		___fun[fnName] = list;
	}
	list[obj.___id] = true;
	return obj[name];
}
var traceCounter = 0;
function ___setter(obj, name, value) {
	traceCounter++;
	if((window.___objs.length > 1000) || (obj === undefined) || (obj === null) || (typeof(obj) !== 'object')) {
		return obj[name] = value;
	}
	if(obj.___id === undefined) {
		obj.___id = window.___ids++;
		window.___objs.push(obj);
		obj.___read = {};
		obj.___write = {};
	}
	var fnName;
	if(window.___fns.length === 0) {
		fnName = "(top)";
	} else {
		fnName = window.___fns[window.___fns.length - 1];
	}
	obj.___write[fnName] = true;
	var list = ___fun[fnName];
	if(!list) {
		list = {};
		___fun[fnName] = list;
	}
	list[obj.___id] = true;
	return obj[name] = value;
}
function ___makeName(stack) {
	var str = "";
	for(var i in stack) {
		if(stack.hasOwnProperty(i)) {
			str += stack[i] + ".";
		}
	}
	return str;
}

function ___labelObjects(root, stack, acc) {
	if(stack.length > 25) {
		return;
	}
	if(!root) {
		return;
	}
	if(root.constructor) {
		if(root.constructor.name === 'DOMPlugin') {
			return;
		}
	}
	if(root.___mark === true) {
		return;
	}
	if(root.___id) {
		acc[root.___id] = ___makeName(stack);
	}
	root.___mark = true;
	if(!root.___mark) {
		return;
	}
	for(var x in root) {
		if(!root.hasOwnProperty) {
			return;
		}
		if(x.indexOf('___') > -1) {
			return;
		}
		if(root.hasOwnProperty(x)) {
			if((x === 'selectionStart') || (x === 'selectionEnd')) {
				return;
			}
			if(typeof(root[x]) === 'object') {
				stack.push(x);
				___labelObjects(root[x], stack, acc);
				stack.pop();
			}
		}
	}
}

function ___print() {
	var str = '';
	var j = 0;
	var funIndex = {};
	var funList = [];
	for(var x in window.___fun) {
		if(window.___fun.hasOwnProperty(x)) {
			funIndex[x] = j;
			funList.push(x);
			j++;
		}
	}
	var numFuns = j;
	for(var x in window.___objs) {
		if(window.___objs.hasOwnProperty(x)) {
			var y = window.___objs[x];
			var readWrite = {};
			for(var i in y.___read) {
				if(y.___read.hasOwnProperty(i) && ___isNumber(funIndex[i])) {
					readWrite[funIndex[i]] = true;
				}
			}
			for(var i in y.___write) {
				if(y.___write.hasOwnProperty(i) && ___isNumber(funIndex[i])) {
					readWrite[funIndex[i]] = true;
				}
			}
			for(var i in readWrite) {
				if(readWrite[i] === true) {
					str += i + " ";
				}
			}
			str += "\n";
		}
	}
	str += "\n";
	for(var i in funList) {
		if(funList.hasOwnProperty(i)) {
			str += funList[i] += "\n";
		}
	}
	str += "\n";
	var objLabels = [];
	var stack = ["window"];
	___labelObjects(window, stack, objLabels);
	for(var i in objLabels) {
		if(objLabels.hasOwnProperty(i)) {
			str += objLabels[i] + "\n";
		}
	}
	var output = window.___objs.length + " " + numFuns + "\n";
	output += str;
	console.log(output);
}