(ns cljs.main
  (:refer-clojure :exclude [val empty remove find])
  (:require [domina :as d]
            [domina.events :as evt]
            [clojure.browser.repl :as repl]))

(defn development? []
  (= document/domain "localhost"))
(def host (str "http://" document/domain))

(defn ^:export setup []
  (when (development?)
    (d/log "Running in dev mode. Connecting to repl")
    (repl/connect (str host ":9000/repl"))))

(set! (.-onload js/window) setup)
