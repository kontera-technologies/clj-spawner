# clj-spawner
Utilities for shelling out in Clojure

## Features
- Callbacks support 
- Timeout
- Easy access to IO streams

## Process Wrapper
clj-spawner wraps the Java process object with this map structure
- `:read-error`  - function - call it to read process STDERR output
- `:read-output` - function - call it to read process STDOUT output
- `:kill`        - function - call it to kill the process
- `:exit`        - function - wait for process to complete and returns the exit code
- `:wait`        - function - wait for process to complete
- `:out-stream`  - returns the STDOUT stream object
- `:err-stream`  - returns the STDERR stream object
- `:in-stream`   - returns the STDIN  stream object
- `:exception`   - when exception occur this field will hold the exception instance

## Usage
shelling out using `exec` which returns a map that wraps the spawned process

```clojure
(require '[clj-spawner.core :as spawner])

(spawner/exec "echo hello-world")

;=> { :read-error  #(...), 
;     :read-output #(...),
;     :kill        #(...),
;     :exit        #(...),
;     :wait        #(...)
;     :out-stream  #<ProcessPipeOutputStream>,
;     :err-stream  #<ProcessPipeInputStream>,
;     :in-stream   #<ProcessPipeInputStream>,
;     :exception   #<JavaException>}

((:read-output (spawner/exec "echo hello-world")))
;=> "hello-world\n"

((:read-error (spawner/exec "cat no-such-file")))
;=> "cat: no-such-file: No such file or directory\n"

((:exit (spawner/exec "cat no-such-file")))
;=> 1
```
shelling out using `exec-with-callbacks` which returns a future object
```clojure
(clj-spawner.core/exec-with-callbacks "echo hello")
;=> returns a future object

@(clj-spawner.core/exec-with-callbacks "sleep 10")
;returns the process map after 10 seconds

((:read-output @(clj-spawner.core/exec-with-callbacks "echo hello")))
;=> "hello\n"
```

passing error & success callbacks
```clojure
(clj-spawner.core/exec-with-callbacks "echo hello" 
  :success #(println (str "HI THIS IS FUN " %1))
  :error   #(println "error"))

;=> HI THIS IS FUN {:read-output...}
```

errors (exit codes != 0)
```clojure
(clj-spawner.core/exec-with-callbacks "cat no-such-file"
  :error   #(println ((:read-error %1)) )
  :success #(println "never called"))

;=> cat: no-such-file: No such file or directory
```

timeout
```clojure
(clj-spawner.core/exec-with-callbacks "sleep 20"
  :timeout 1
  :error #(println (:exception %1)))

;=> #<TimeoutException java.util.concurrent.TimeoutException>
```

## Installation 
clj-spawner is available as a Maven artifact from [Clojars](http://clojars.org/clj-spawner), if you are using [leiningen](http://leiningen.org/) just add `clj-spawner` as a dependency to your project.clj file

```clojure
:dependencies [[clj-spawner "0.0.2"]]
```
