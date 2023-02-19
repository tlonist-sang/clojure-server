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
        n  count]
    {:user-id id
     :content (random-str 30)}))

(defn generate-post-comments [])

(comment
  (->> (repeatedly 10 #(nth "abcdefghijklmnopqrstuvwxyz" (rand-int 26)))
       (apply str))

  (random-str 123)
  (generate-users 10)


  (for [id [1 2 3]
        n  [10 20 30]]
    [id "hello"])

  (let [users    (generate-users 100)
        posts    (generate-posts user-ids 10)
        comments (generate-post-comments)]))
