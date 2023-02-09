(ns handler
  (:require [reitit.ring :as ring]
            [reitit.coercion.spec]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.ring.coercion :as coercion]
            [reitit.dev.pretty :as pretty]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.multipart :as multipart]
            [reitit.ring.middleware.parameters :as parameters]))

(defn default []
  ["/" {:get {:no-doc    true
              :summary   "home"
              :responses {200 [:map
                               [:body [:map
                                       [:message string?]]]]}
              :handler   (fn [_] {:status 200
                                  :body   "Hello, world!"})}}])

(defn swagger []
  ["/swagger.json"
   {:get {:no-doc  true
          :swagger {:info {:title       "clojure-server"
                           :description "with reitit-ring"}}
          :handler (swagger/create-swagger-handler)}}])

(def app
  (ring/ring-handler
    (ring/router
      [(swagger)
       (default)])
    (ring/routes
      (swagger-ui/create-swagger-ui-handler {:path "/swagger-ui"})
      (ring/create-default-handler))))

(comment)
