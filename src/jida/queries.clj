(ns jida.queries
  (:require [monger.collection :as mc]
            [monger.query :as mq])
  (:use [monger.core :only [connect-via-uri!]]
        [noir.fetch.remotes]
        monger.operators)
  (:import [org.bson.types ObjectId]))

(def mongo-uri (or (System/getenv "MONGOHQ_URL")
                   "mongodb://localhost:27017/jida"))

(defn id->object-id [id]
  (if (string? id) (ObjectId. id) id))

(defn object-id->id [object-id]
  (str object-id))

(defn add-create-time [{id :_id :as query}]
  (if id
    (assoc query :created (.getTime id))
    query))

(defn make-serializeable [query]
  (update-in query [:_id] object-id->id))

(def serialize (comp make-serializeable add-create-time))

(defn connect-mongo! []
 (connect-via-uri! mongo-uri))

(defremote save-query [{query :query :as query-item}]
  {:pre [query]}
  (serialize (mc/save-and-return "queries" query-item)))

(defremote get-query [id]
           (try
             (serialize
               (mc/find-map-by-id "queries" (id->object-id id)))
             (catch Exception e nil)))

(defremote all-queries []
  (map serialize (mc/find-maps "queries")))

(defremote recent-queries []
           (map serialize
                (mq/with-collection "queries"
                                    (mq/find {})
                                    (mq/sort (sorted-map :_id -1))
                                    (mq/limit 10))))
