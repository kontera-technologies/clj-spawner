# clj-spawner
Utilities for shelling out in Clojure

## Features
- Callbacks support 
- Timeout
- Easy access to IO streams

## Usage
Shelling out

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
Future
```clojure
(clj-spawner.core/exec-with-callbacks "echo hello")
;=> returns a future object
```
Passing error & success callbacks
```clojure
(clj-spawner.core/exec-with-callbacks "echo hello" 
  :success #(println (str "HI THIS IS FUN " %1))
  :error   #(println "error"))

;=> HI THIS IS FUN {:read-output...}
```

Errors (exit codes != 0)
```clojure
(clj-spawner.core/exec-with-callbacks "cat no-such-file"
  :error   #(println ((:read-error %1)) )
  :success #(println "never called"))

;=> cat: no-such-file: No such file or directory
```

Timeout
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
