(ns jida.server
  (:require [noir.server :as server]
            [jida.datomic :as jida]
            [clj-redis.client :as redis])
  (:use [noir.fetch.remotes]))

; Ideas
; 1. Function-specific queries: explore functions
;   a. Function history: Most frequent commit author ("Who changed this function most?")
;   b. Calculate code complexity for a function over time,
;        sort by current most complex functions,
;        who contributed most complexity,
;        when?
; 2. Repo-wide queries: explore projects
; 3. Author-specific queries: explore contributor history
; 4. Import repos

(def redis-uri (or (System/getenv "REDIS_URI") "redis://localhost"))
(defonce redis-conn (atom nil))
(defn connect-redis []
  (reset! redis-conn (redis/init :url redis-uri)))

(server/load-views-ns 'jida.views)

(defonce conn (atom nil))

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8080"))]
    (reset! conn (jida/connect))
    (connect-redis)
    (server/start port {:mode mode
                        :ns 'jida})))

(defremote query-codeq [q]
  (println "Received query for" q)
  (let [result (try (jida/query q @conn)
                 (catch Exception e {:error (str e)}))]
    (println)
    (println "Finished: " result)
    result))

(defremote queue-import [address]
  (redis/rpush @redis-conn "tasks" address))
