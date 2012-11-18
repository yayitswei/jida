(ns cljs.main
  (:refer-clojure :exclude [val empty remove find])
  (:require [cljs.helpers :as helper]
            [clojure.browser.repl :as repl]
            [domina :as d]
            [domina.events :as evt]
            [fetch.remotes :as remotes]
            [goog.dom.selection :as selection])

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
  (d/set-html! (d/by-id "results") (items results))
  (helper/show (d/by-id "results")))


(defn select-character [text-area offset]
  (selection/setStart text-area offset)
  (selection/setEnd text-area (inc offset)))


(defn submit-query [_]
  (let [query (d/value (d/by-id "query-text"))
        [valid-query? error-offsets] (helper/balanced-parens? query)]
    (if valid-query?
      (do
        (helper/show (d/by-id "loader"))
        (fm/remote
          (query-codeq query) [results]
          (display-results results)
          (helper/hide (d/by-id "loader"))
          (helper/hide (d/by-id "error-messages"))))
      (do
        (helper/show (d/by-id "error-messages"))
        (d/set-html! (d/by-id "error-offsets")
                     (clojure.string/join ", " error-offsets))
        (select-character (d/by-id "query-text") (first error-offsets))
        (helper/hide (d/by-id "loader"))))))

(defn queue-import [_]
  (helper/show (d/by-id "import-status"))
  (d/set-text! (d/by-id "import-status") "Queueing import")
  (let [address (d/value (d/by-id "repo-address"))]
    (fm/remote
      (queue-import address) [_]
      (d/set-text! (d/by-id "import-status")
                   (str "Importing " address ".. you may not see it right away.")))))

(defn ^:export setup []
  (fm/remote
   (query-codeq "[:find ?repo-names :where [?repos :repo/uri ?repo-names]]") [result]
   (d/set-html! (d/by-id "available-repos")
                (repos result)))
  (evt/listen! (d/by-id "query-submit") :click submit-query)
  (evt/listen! (d/by-id "import-repo-btn") :click queue-import)
  (when (development?)
    (repl/connect (str host ":9000/repl"))))

(set! (.-onload js/window) setup)
