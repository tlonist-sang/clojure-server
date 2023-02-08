(ns core
  (:require [ring.adapter.jetty :as jetty]))

(def ^:dynamic *server*)

(defn handler [request]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "Hello World"})

(defn start-server []
  (alter-var-root
    *server*
    (jetty/run-jetty handler
                      {:port                 3000
                       :join?                false
                       :send-server-version? false})))

(defn stop-server []
  (.stop *server*))

(comment
  (start-server))

