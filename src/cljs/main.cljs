(ns cljs.main
  (:refer-clojure :exclude [val empty remove find])
  (:require [clojure.browser.repl :as repl]
            [domina :as d]
            [domina.events :as evt]
            [fetch.remotes :as remotes])

  (:use-macros [crate.def-macros :only [defpartial]])
  (:require-macros [fetch.macros :as fm]))

(defn development? []
  (= document/domain "localhost"))

(def host (str "http://" document/domain))

(defpartial item [fields]
            [:div.result-item
             [:table.table.table-bordered.table-striped
              [:tbody
               (map #(vector :tr [:td %]) fields)]]])

(defpartial items [items]
            [:div.results
             [:p (count items) " returned."]
             [:div.result-items (map item items)]])

(defpartial repo [[repo]]
  [:a {:href repo :target "_blank"} repo])

(defpartial repos [repos]
  [:div (interpose ", " (map repo repos))])

(defn display-results [results]
  (d/log "Display results: " results)
  (d/set-html! (d/by-id "results") (items results))
  (d/set-style! (d/by-id "results") "display" "block"))

(defn visible? [node-seq]
  (let [display (d/style node-seq "display")]
    (when (not (= display "none")) true)))

(defn toggle [node-seq]
  "Toggles the visibility of a node-seq using Domina"
  (if (visible? node-seq)
    (hide node-seq)
    (show node-seq)))

(defn show [node-seq]
  "Sets the display style attr of a node-seq to block (visible)"
  (d/set-style! node-seq "display" nil))

(defn hide [node-seq]
  "Sets the display style attr of a node-seq to none (invisible)"
  (d/set-style! node-seq "display" "none"))

(defn submit-query [_]
  (show (d/by-id "loader"))
  (fm/remote
    (query-codeq (d/value (d/by-id "query-text"))) [results]
    (display-results results)
    (hide (d/by-id "loader"))))


(defn ^:export setup []
  (fm/remote
   (query-codeq "[:find ?repo-names :where [?repos :repo/uri ?repo-names]]") [result]
   (d/log "result: " result)
   (d/set-html! (d/by-id "available-repos")
                (repos result)))

  (evt/listen! (d/by-id "query-submit") :click submit-query)
;;  (display-results sample-results)
  (when (development?)
    (d/log "Running in dev mode. Connecting to repl")
    (repl/connect (str host ":9000/repl"))))

(set! (.-onload js/window) setup)
