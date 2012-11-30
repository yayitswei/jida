(ns jida.queries
  (:require [monger.collection :as mc])
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

(defn add-create-time [query]
  (assoc query :created (-> query :_id .getTime)))

(defn make-serializeable [query]
  (update-in query [:_id] object-id->id))

(defn connect-mongo! []
 (connect-via-uri! mongo-uri))

(defremote save-query [{query :query :as query-item}]
  {:pre [query]}
  (mc/save-and-return "queries" query-item))

(defremote get-query [id]
  (mc/find-by-id "queries" (id->object-id id)))

(defremote all-queries []
  (map make-serializeable (mc/find-maps "queries")))
