# herpderp

Herpderp is a logic-based monitoring and testing framework. It leverages Clojure's core.logic to enable reasoning about the temporal behavior and control-flow of your software. The aim is to have a general-purpose reasoner in which you can specify conditions that should always hold while your program is executing, as well as an array of client-libraries to feed this reasoner with data, either in batch-mode via logs, or as messages to a running server.

## Status

Herpderp still is in prototyping. I am working out the temporal logic worked out, as well as the general workflow. 

Once that is more or less built, I'll develop a Clojure and a server-side JavaScript logging-client. Next it would be interesting whether one could integrate herpderp into a debugging-server setup.

## License

Copyright (C) 2012 Gabriel F. Pickard

Distributed under the Eclipse Public License, the same as Clojure.
