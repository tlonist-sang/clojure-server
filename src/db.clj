(ns db
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [honey.sql :as h]
            [camel-snake-kebab.core :as csk]
            [clojure.string])
  (:import java.util.UUID))

(defn bytes->uuid
  [b]
  (let [hexed (->> (map #(format "%02x" %) b)
                   (apply str))]
    (clojure.string/join "-" [(subs hexed 0 8)
                              (subs hexed 8 12)
                              (subs hexed 12 16)
                              (subs hexed 16 20)
                              (subs hexed 20)])))

(defn uuid->bytes
  [s]
  (->> (clojure.string/replace s #"-" "")
       (partition 2)
       (map #(Integer/parseInt (apply str %) 16))
       byte-array))


(def db
  (jdbc/get-datasource {:dbtype   "mysql"
                        :dbname   "morningnote"
                        :host     "localhost"
                        :port     3306
                        :user     "root"
                        :password ""}))

(defn update-user [{:keys [user-id email first-name last-name]}]
  (jdbc/execute! db
                 (h/format {:update :*
                            :set    {:email      email
                                     :first_name first-name
                                     :last_name  last-name}
                            :where  [:= :id user-id]})
                 {:builder-fn rs/as-unqualified-kebab-maps}))

(defn insert-users [users]
  (jdbc/execute! db
                 (h/format {:insert-into :users
                            :columns     (->> users first keys (map csk/->snake_case))
                            :values      (map vals users)})
                 {:builder-fn rs/as-unqualified-kebab-maps}))

(defn insert-posts [posts]
  (jdbc/execute! db
                 (h/format {:insert-into :posts
                            :columns     (->> posts first keys)
                            :values      (map vals posts)})
                 {:builder-fn rs/as-unqualified-kebab-maps}))

(defn get-all-users []
  (jdbc/execute! db
                 (h/format {:select :*
                            :from   :users})
                 {:builder-fn rs/as-unqualified-kebab-maps}))


(defn fetch-user-by-id [user-id]
  (jdbc/execute! db
                 (h/format {:select :*
                            :from   :users
                            :where  [:= :id user-id]})
                 {:builder-fn rs/as-unqualified-kebab-maps}))

(defn get-all-posts []
  (jdbc/execute! db
                 (h/format {:select :*
                            :from   :posts})
                 {:builder-fn rs/as-unqualified-kebab-maps}))

(defn insert-post-comments [comments]
  (jdbc/execute! db
                 (h/format {:insert-into :post_comments
                            :columns     (-> comments first keys)
                            :values      (map vals comments)})
                 {:builder-fn rs/as-unqualified-kebab-maps}))

(comment
  (fetch-user-by-id (-> (get-all-users) first :id))

  (jdbc/execute! db
                 (h/format {:insert-into :posts
                            :columns     [:user_id :content]
                            :values      [[(-> (get-all-users) first :id) "abc"]]})
                 {:builder-fn rs/as-unqualified-kebab-maps})



  (UUID/randomUUID)
  :rcf)
