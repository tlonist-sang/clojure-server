(ns graphql
  (:require [com.walmartlabs.lacinia.util :as util]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia :as lacinia]
            [ring.util.http-response :as hr]
            [clojure.data.json :as json]
            [ring.util.request :as request]
            [com.walmartlabs.lacinia.resolve :refer [resolve-as]]
            [clojure.edn :as edn]))

(def resolver-map
  {:greet (fn [_ _ _] "Hello, from GraphQL!")})

(defn load-schema
  []
  (-> "resources/query.edn"
      slurp
      edn/read-string
      (util/attach-resolvers resolver-map)
      schema/compile))

(defn handler [request]
  (let [schema (load-schema)
        graphql-request (json/read-str (request/body-string request) :key-fn keyword)
        {:keys [query variables]} graphql-request
        _ (prn graphql-request)
        result (lacinia/execute schema query variables nil)]
    (hr/ok (json/write-str result))))


(defn route [handler]
  ["/graphql" {:post {:handler handler}}])

(comment
  (load-schema))
