(ns cljs.main
  (:refer-clojure :exclude [val empty remove find])
  (:require [domina :as d]
            [domina.events :as evt]
            [fetch.remotes :as remotes]
            [clojure.browser.repl :as repl])
  (:require-macros [fetch.macros :as fm]))

(defn development? []
  (= document/domain "localhost"))
(def host (str "http://" document/domain))

(defn submit-query [_]
  (fm/remote (query-codeq (d/value (d/by-id "query-text"))) [result]
             (d/set-text! (d/by-id "results") result)
             (d/set-style! (d/by-id "results") "display" "block")))

(defn ^:export setup []
  (evt/listen! (d/by-id "query-submit") :click submit-query)

  (when (development?)
    (d/log "Running in dev mode. Connecting to repl")
    (repl/connect (str host ":9000/repl"))))

(set! (.-onload js/window) setup)
