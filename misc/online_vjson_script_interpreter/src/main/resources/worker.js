// run in worker space
importScripts('online_vjson_script_interpreter.js');
var interpreter = online_vjson_script_interpreter;

interpreter.registerOutput(function (line) {
  self.postMessage(['output', line]);
});
interpreter.registerCursorJump(function (line, col) {
  self.postMessage(['gotoLine', line, col]);
});

self.onmessage = function(e) {
  var data = e.data;
  if (!(data instanceof Array)) return;

  var action = data[0];
  var workerVersion = data[1];
  if (action === 'run') {
    _run(data[2], data[3]);
  } else if (action === 'eval') {
    _eval(data[2]);
  } else if (action === 'json') {
    _json(data[2]);
  } else if (action === 'ast') {
    _ast(data[2]);
  } else {
    console.error('unknown action %j in worker scope (msg comes from main scope)', action);
  }

  _done(workerVersion);
};

function _run(code, printMem) {
  interpreter.run(code, printMem);
}

function _eval(code) {
  interpreter.eval(code)
}

function _json(code) {
  interpreter.json(code)
}

function _ast(code) {
  interpreter.ast(code)
}

function _done(ver) {
  postMessage(['done', ver]);
}
