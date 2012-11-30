(ns cljs.main
  (:refer-clojure :exclude [val empty remove find])
  (:require [cljs.helpers :as helper]
            [clojure.browser.repl :as repl]
            [domina :as d]
            [domina.events :as evt]
            [fetch.remotes :as remotes]
            [goog.Uri :as uri]
            [goog.dom.selection :as selection])

  (:use-macros [crate.def-macros :only [defpartial]])
  (:require-macros [fetch.macros :as fm]))

(defn development? []
  (= document/domain "localhost"))

(def host (str "http://" document/domain))

(def parsed-uri
  (goog.Uri. (-> (.-location js/window) (.-href) )))

(def initial-query-id
  (.getParameterValue parsed-uri "query-id"))

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

(defn display-error [message]
  (helper/show (d/by-id "error-messages"))
  (d/set-html! (d/by-id "error-messages") message))

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
           (display-error (str "Your parens at offet(s) " (clojure.string/join ", " error-offsets) " aren't properly balanced, please check again"))
        (select-character (d/by-id "query-text") (first error-offsets))
        (helper/hide (d/by-id "loader"))))))

(defn queue-import [_]
  (let [url (d/value (d/by-id "repo-address"))]
    (if (helper/valid-git-url? url)
      (do
        (helper/show (d/by-id "import-status"))
        (d/set-text! (d/by-id "import-status") "Queueing import")
        (fm/remote
         (queue-import url) [_]
         (d/set-text! (d/by-id "import-status")
                      (str "Importing " url "... you may not see it right away."))))
      ; Invalid url
      (display-error "That looks like an invalid Git url. It must start with 'https://'"))))

(defn set-query-field [query]
  (d/set-text! (d/by-id "query-text") query))

(defn check-save-button []
  (d/log "Checking the save button")
  (let [query-node (d/by-id "query-text")
        query-value (d/value query-node)]
    (if (= (count query-value) 0)
      (d/set-attr! (d/by-id "query-save") :disabled true)
      (d/remove-attr! (d/by-id "query-save") :disabled))))

(defn save-query! []
  (let [title (js/prompt "Query title")
        description (js/prompt "Describe the query")
        query-value (d/value (d/by-id "query-text"))]
    (letrem [result (save-query {:query query-value :description description :title title})]
            (d/log "Save result: " result))))

(defn ^:export setup []
  (fm/remote
   (query-codeq "[:find ?repo-names :where [?repos :repo/uri ?repo-names]]") [result]
   (d/set-html! (d/by-id "available-repos")
                (repos result)))
  (evt/listen! (d/by-id "query-submit") :click submit-query)
  (evt/listen! (d/by-id "query-save") :click save-query!)
  (evt/listen! (d/by-id "import-repo-btn") :click queue-import)
  (evt/listen! (d/by-id "query-text") :keypress check-save-button)
  (evt/listen! (d/by-id "query-text") :focus check-save-button)
  (evt/listen! (d/by-id "query-text") :blur check-save-button)
  (when initial-query-id
    ;(letrem [query (get-query initial-query-id)]           (set-query-field query))
    (set-query-field "This is an initial query!")
    )
  (when (development?)
    (repl/connect (str host ":9000/repl"))))

(set! (.-onload js/window) setup)
