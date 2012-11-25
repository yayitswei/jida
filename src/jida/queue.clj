(ns jida.queue
  (:require [noir.server :as server]
            [jida.datomic :as jida]
            [taoensso.carmine :as car])
  (:use [noir.fetch.remotes]))

(defmacro wcar [& body] `(car/with-conn pool conn-spec ~@body))
(def redis-uri (java.net.URI. (or (System/getenv "REDIS_URI") "redis://96.126.103.193:6379")))
(def pool      (car/make-conn-pool))
(def conn-spec (car/make-conn-spec :host (.getHost redis-uri)
                                   :port (.getPort redis-uri)))

(defn queue-import [address]
  (wcar (car/rpush "tasks" address)))
