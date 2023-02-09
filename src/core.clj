(ns core
  (:require [ring.adapter.jetty :as jetty]
            [handler :as handler]))

(def ^:dynamic *server*)

(defn start-server []
  (alter-var-root
    #'*server*
    (constantly (jetty/run-jetty #'handler/app
                                  {:port                 3000
                                   :join?                false
                                   :send-server-version? false}))))
(defn stop-server []
  (.stop *server*))

(comment
  (start-server)
  (stop-server)
  ,)

