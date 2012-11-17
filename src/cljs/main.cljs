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

(defn ^:export setup []
  (fm/remote (query-codeq "test query") [result] (d/log result))
  (when (development?)
    (d/log "Running in dev mode. Connecting to repl")
    (repl/connect (str host ":9000/repl"))))

(set! (.-onload js/window) setup)
