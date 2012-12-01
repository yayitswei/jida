(ns cljs.main
  (:refer-clojure :exclude [val empty remove find])
  (:require [cljs.helpers :as helper]
            [clojure.browser.repl :as repl]
            [domina :as d]
            [domina.events :as evt]
            [fetch.remotes :as remotes]
            [goog.Uri :as uri]
            [goog.dom.selection :as selection]
            [cljs.reader :as reader]
            )

  (:use-macros [crate.def-macros :only [defpartial]])
  (:require-macros [fetch.macros :as fm]))

(defn development? []
  (= document/domain "localhost"))

(def host (str "http://" document/domain))

(def parsed-uri
  (goog.Uri. (-> (.-location js/window) (.-href) )))

(def initial-query-id
  (.getParameterValue parsed-uri "query-id"))

(defpartial results-item [fields]
            [:tr (map #(vector :td %) fields)])

(defpartial results-items [items headers]
            [:div.results
             [:p (count items) " returned."]
            [:table.table.table-bordered.table-striped
             [:thead
              [:tr (map #(vector :th %) headers)]]
             [:tbody (map results-item items)]]])

(defpartial results-error [{msg :error}]
            [:div.results.alert.alert-error msg])

(defpartial repo [[repo]]
            [:a {:href repo :target "_blank"} repo])

(defpartial repos [repos]
            [:div (interpose ", " (map repo repos))])

(defn query-link [id]
  (str "/?query-id=" id))

(defn friendly-title [title]
  (if (empty? title) "untitled" title))

(defn friendly-description [description]
  (if (empty? description) "" (str ": " description)))

(defpartial query-history-item [{:keys [title description _id]}]
            [:li
             [:a {:href (query-link _id)} (friendly-title title)]
             (friendly-description description)
             ])

(defpartial query-history [items]
            [:ul (map query-history-item items)])

(defn safe-read [s]
  (binding [reader/*read-eval* false]
    (reader/read-string s)))

(defn extract-find-args [query]
  (let [q (safe-read query)]
    (map str
         (take-while #(not (keyword? %))
                     (drop 1 (drop-while #(not (= :find %)) q))))))

(defn display-results [results query]
  (d/log query (extract-find-args query))
  (d/set-html! (d/by-id "results")
               (if (:error results)
                 (results-error results)
                 (results-items results (extract-find-args query))))
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
          (display-results results query)
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

(defn set-query-title! [title]
  (d/set-text! (d/by-id "query-title") title))

(defn set-query-description! [description]
  (d/set-text! (d/by-id "description") description))

(defn check-save-button []
  (d/log "Checking the save button")
  (let [query-node (d/by-id "query-text")
        query-value (d/value query-node)]
    (if (= (count query-value) 0)
      (d/set-attr! (d/by-id "query-save") :disabled true)
      (d/remove-attr! (d/by-id "query-save") :disabled))))

(defn update-query-history! []
  (fm/letrem [history (all-queries)]
             (d/log history)
             (d/set-html! (d/by-id "query-history") (query-history history))))

(defn save-query! []
  (let [title (js/prompt "Query title")
        description (js/prompt "Describe the query")
        query-value (d/value (d/by-id "query-text"))]
    (fm/letrem [result (save-query {:query query-value :description description :title title})]
               (update-query-history!)
               result)))

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
    (d/log "Loading existing query for " initial-query-id)
    (fm/letrem [{:keys [title description query]} (get-query initial-query-id)]
               (d/log initial-query-id query)
               (d/log query)
               (set-query-field query)
               (set-query-description! (friendly-description description))
               (set-query-title! (friendly-title title))))

  (update-query-history!)

  (comment (when (development?)
    (repl/connect (str host ":9000/repl")))))

(set! (.-onload js/window) setup)
