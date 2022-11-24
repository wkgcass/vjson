// run in worker space
importScripts('online_vjson_lang_interpreter.js');
var interpreter = online_vjson_lang_interpreter;

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
    run(data[2], data[3]);
  } else if (action === 'eval') {
    eval(data[2]);
  } else if (action === 'json') {
    json(data[2]);
  } else if (action === 'ast') {
    ast(data[2]);
  } else {
    console.error('unknown action %j in worker scope (msg comes from main scope)', action);
  }

  done(workerVersion);
};

function run(code, printMem) {
  interpreter.run(code, printMem);
}

function eval(code) {
  interpreter.eval(code)
}

function json(code) {
  interpreter.json(code)
}

function ast(code) {
  interpreter.ast(code)
}

function done(ver) {
  postMessage(['done', ver]);
}
