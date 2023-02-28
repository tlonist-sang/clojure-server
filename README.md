## tech-stack
- Server
  - Ring(jetty)
  - Aleph
  - Http-kit
  - Pedestal

- REST API Router
  - reitit

- GraphQL
  - relay server specification
  - dataloader


## On superlifter
- Superlifter library is based on [Urania](https://github.com/funcool/urania) and [Promesa](https://github.com/funcool/promesa). 

### def-fetcher
- defines how to fetch the top-most (hence unbatched) data
```clojure
(def-fetcher User [_ids] ;; ids are a vector of plain values. The symbol ids become the key that is referred to in the function below. 
             (fn [data env]
               ;; data is in the format of {:_ids [.. vector_of_data]}  
               ;; one can use _ids directly
               
               ;; env is the superlifter environment that is referenced from 
               ;; (with-superlifter context ..) here, the context is passed onto as env
               ;; one can take db instance, or whaterver serves the purpose from the env.
               
               ;; given a vector ids, one can fetch data that is not outsourced to field resolvers.
               ;; return the vector of data as a result
               :rcf))
```

### def-superfetcher
- defines how to fetch the batched data
```clojure
(def-superfetcher Posts [_id]
                  (fn [_ids env]
                    ;; the _id symbol becomes the key of _ids field below.
                    ;; {:ids [... vector of ids data]}
                    ;; given a vector of ids, fetch posts data and map with the ids provided
                    ;; the data has to be in order with the ids given.
                   :rcf))
```

### On other functions

- **enqueue** : "Enqueues a muse describing work to be done and returns a promise which will be delivered with the result of the work.
  The muses in the queue will all be fetched together when the trigger condition is met."
  - what a bullshit, the explanation above is terrible and does not give any hints on how the function should be used.
```clojure
(enqueue! :bucket_name (->Fetcher-or-SuperFetcher args))
;; what it does is that the result of (->Fetcher-or-SuperFetcher args), a vector of data, is enqeued in the bucket
;; bucket is predefined in the superlifter configuration when superlifter is started.
;; it can be a single value, or a multiple of values.
```

- **update-trigger** : "no explanation whatsoever, no document at all. awesome!"
  - This function sets condition for the bucket.
  - Its primal usecase is when triggering strategy is elastic. Setting up the threshold for elastic, it declares how much data should be batched from the 'superfetcher',
```clojure
(update-trigger! :bucket_name :trigger-strategy
                 (fn [trigger-opts _args]
                   (update trigger-opts :threshold (count _args)))
                   ;; args is the vector of data fetched from def-fetcher OR def-superfetcher
                   ;; it is the result of enqueue that happens right above it.
                   :rcf)
```

- combining enqueue and update-trigger
```clojure

;; depth 0 (fetcher)
(-> (enqueue! :level-1-bucket (->Fetcher args))
    (update-trigger! :level-2-bucket :elastic
                     (fn [trigger-opts _data]
                       (update trigger-opts :threshold + (count _data))))) ;;when elastic

;; depth 1 (superfetcher)
(-> (enqueue! :level-2-bucket (->SuperFetcher1 _arg))
    (update-trigger! :level-3-bucket :elastic
                     (fn [trigger-opts _data]
                       (update trigger-opts :threshold ...))));; arg is a single datum
  

;; depth 2 (superfetcher of superfetcher)
(-> (enqueue! :level-3-bucket (->SuperFetcher2 _arg)))

;; depth 3... and so on..

```

### caution 1
- When using a server implementation other than pedestal (like ring), make sure to add a layer that resolves promise.
- Superlifter returns a promise, hence its result has to be derefed and cast to lacinia implementation to be processed.

```clojure
;; private function to deliver a resolved promise
(defn ->lacinia-promise [sl-result]
  (let [l-prom (resolve/resolve-promise)]
    (s/unwrap #(resolve/deliver! l-prom %) sl-result)
    l-prom))

;; a macro that re-defines with-superlifter for convenience
(defmacro with-superlifter [ctx body]
  `(s/with-superlifter ~ctx
                       (->lacinia-promise ~body)))
```

### caution 2
- This library makes lots of use of atom and future.
- The core namespace contains a line in which the `swap!` operation causes side-effects, namely, creating a future.
- swap! has to take a side-effect free function to prevent it from executing multiple times under a race condition.
- Currently (23/02) the library has a bug that creates many unintentional threads.