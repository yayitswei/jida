(ns cljs.main
  (:refer-clojure :exclude [val empty remove find])
  (:require [cljs.helpers :as helper]
            [clojure.browser.repl :as repl]
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
  (d/set-html! (d/by-id "results") (items results))
  (helper/hide (d/by-id "results")))

(defn balanced-parens? [query]
  "Actually, this doesn't work. [[ and [{]} are both considered passing here. Need to make it smarter."
  (let [pairs [[\{ \}]
               [\[ \]]
               [\( \)]]]
    (every? (fn [r] (d/log r) (true? r)) (map (fn [pair]
                         (even? (count (filter #(some #{%} pair) query)))) pairs))))

(defn submit-query [_]
  (helper/show (d/by-id "loader"))
  (let [query (d/value (d/by-id "query-text"))
        valid-query? (balanced-parens? query)]
    (if valid-query?
      (fm/remote
       (query-codeq query) [results]
       (display-results results)
       (helper/hide (d/by-id "loader"))
       (helper/hide (d/by-id "error-messages")))
                                        ; Inline because it's late and I'm tired
      (do
        (helper/show (d/by-id "error-messages"))
        (helper/hide (d/by-id "loader"))))))


(defn ^:export setup []
  (fm/remote
   (query-codeq "[:find ?repo-names :where [?repos :repo/uri ?repo-names]]") [result]
   (d/set-html! (d/by-id "available-repos")
                (repos result)))
  (evt/listen! (d/by-id "query-submit") :click submit-query)
  (when (development?)
    (repl/connect (str host ":9000/repl"))))

(set! (.-onload js/window) setup)
