(ns clj-spawner.core)
  (:require [clojure.java.io   :as io]
            [clj-spawner.utils :as utils]))

(defn -wrap [process]
  {:out-stream  (.getOutputStream process)
   :err-stream  (.getErrorStream  process)
   :in-stream   (.getInputStream  process)
   :kill       #(.destroy process)
   :exit       #( do (.waitFor process)
                     (.exitValue process))
   :wait       #(.waitFor process)})

(defn -wrap-streams [process-map]
  (merge process-map
         {:read-output #( slurp (io/reader (:in-stream  process-map)))
          :read-error  #( slurp (io/reader (:err-stream process-map)))}))

(defn exec [command]
  (let [runtime (java.lang.Runtime/getRuntime)
        process (.exec runtime command)]

    (.addShutdownHook runtime (Thread. #(.destroy process)))
    (-> process -wrap -wrap-streams)))

(defn exec-with-callbacks
  [command & {:keys [success error timeout]
              :or   {success identity error identity timeout 320}}]
  (let [process# (exec command)]
    (future
      (try
        (utils/with-timeout timeout ((:wait process#)))
        (if (zero? ((:exit process#)))
          (success process#)
          (error   process#))
        (catch Exception e# 
          (error (merge process# {:exception e# :kill ((:kill process#))}))))
      process#)))
