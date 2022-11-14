var worker;
var workerVersion = 0;
var workerTimeout = 0;

function initWorker() {
  if (worker) {
    worker.terminate;
  }
  ++workerVersion;
  worker = new Worker('worker.js');
  worker.onmessage = onWorkerMessage;
}
initWorker();

const executionTimeoutSeconds = 10;
function workerStart() {
  var captureVersion = workerVersion;
  workerTimeout = setTimeout(function() {
    initWorker();
    workerTimeout = 0;
    output('execution of worker#' + captureVersion + ' exceeds ' + executionTimeoutSeconds + ' seconds');
  }, executionTimeoutSeconds * 1000);
}

function workerRun(code, printMem) {
  if (workerTimeout) {
    output('another program is running, please wait...')
    return;
  }
  workerStart();
  worker.postMessage(['run', workerVersion, code, printMem]);
}

function workerEval(code) {
  if (workerTimeout) {
    output('another program is running, please wait...')
    return;
  }
  workerStart();
  worker.postMessage(['eval', workerVersion, code]);
}

function workerJson(code) {
  if (workerTimeout) {
    output('another program is running, please wait...')
    return;
  }
  workerStart();
  worker.postMessage(['json', workerVersion, code]);
}

function workerAst(code) {
  if (workerTimeout) {
    output('another program is running, please wait...')
    return;
  }
  workerStart();
  worker.postMessage(['ast', workerVersion, code]);
}

function onWorkerMessage(e) {
  var data = e.data;
  if (!(data instanceof Array)) return;

  var action = data[0];
  if (action === 'output') {
    output(data[1]);
  } else if (action === 'gotoLine') {
    gotoLine(data[1], data[2]);
  } else if (action === 'done') {
    workerDone(data[1]);
  } else {
    console.error('unknown action %j in main scope (msg comes from worker scope)', action);
  }
};

function output(line) {
  var newLines = aceConsole.getSession().getValue() + line + '\n';
  aceConsole.getSession().setValue(newLines);
}

function gotoLine(line, col) {
  editor.gotoLine(line, col - 1, true);
  editor.focus();
}

function workerDone(ver) {
  if (workerVersion !== ver) return;

  if (workerTimeout) {
    clearTimeout(workerTimeout);
    workerTimeout = 0;
  }
}
