# herpderp

Herpderp is a logic-based monitoring and testing framework. It leverages Clojure's core.logic to enable reasoning about the temporal behavior and control-flow of your software. The aim is to have a general-purpose reasoner in which one can specify conditions that should always hold while your program is executing, as well as an array of client-libraries to feed this reasoner with data, either in batch-mode via logs, or as messages to a running server.

## Status

Herpderp still is in prototyping. I am working out the temporal logic, as well as the general workflow. 

Once that is more or less built, I'll develop a Clojure and a server-side JavaScript logging-client.

### Building a logical debugger

 Next it would be interesting whether one could integrate herpderp into a debugging-server setup, this should be rather easy for node.js using the V8 [debugging protocoll](http://code.google.com/p/v8/wiki/DebuggerProtocol), get further info from the [wiki](http://code.google.com/p/v8/wiki/AddDebuggerSupport) and the node-inspector [source](https://github.com/dannycoates/node-inspector/blob/master/lib/debugger.js).

 ### Code quality

 I am building this partially to gain experience with the core.logic framework. I come more from a Prolog-background, hence I'm unfamiliar with many of the appropriate Clojure core.logic, or even Lisp idioms. Friendly pointers are much appreciated!

## License

Copyright (C) 2012 Gabriel F. Pickard

Distributed under the Eclipse Public License, the same as Clojure.
