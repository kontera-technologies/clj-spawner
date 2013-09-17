(ns clj-spawner.utils)

(defmacro with-timeout [seconds & body]
  `(let [future# (future ~@body)]
     (.get future# ~seconds java.util.concurrent.TimeUnit/SECONDS)))

