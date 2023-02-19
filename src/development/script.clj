(ns development.script)

(defn random-str [length]
  (->> (repeatedly length #(nth "abcdefghijklmnopqrstuvwxyz" (rand-int 26)))
       (apply str)))

(defn generate-users [count]
  (for [i (range count)]
    {:first-name (random-str 5)
     :last-name  (random-str 7)
     :email      (str (random-str 6) "@" (random-str 7) ".com")}))

(defn generate-posts [user-ids count]
  (for [id user-ids
        n  (range count)]
    {:user-id id
     :content (random-str 30)}))

(defn generate-comments
  "generate count amount of post comments with random user-ids on random post-ids"
  [user-ids post-ids count]
  (for [n (range count)]
    {:user-id (rand-nth user-ids)
     :post-id (rand-nth post-ids)
     :content (random-str 20)}))


(comment
  (require '[db :as db]
           '[camel-snake-kebab.core :as csk])

  (def users (generate-users 50))
  (def posts (db/get-all-posts))
  (def users (db/get-all-users))
  (db/insert-users users)

  (generate-posts (->> (db/get-all-users)
                       (map :id)) (range 5))

  (db/insert-posts (->> (generate-posts (->> (db/get-all-users)
                                             (map :id)) (range 5))))

  (db/insert-post-comments (generate-comments (map :id users) (map :id posts) 100))
  :rcf)