(ns jikken.views.welcome
  (:require [jikken.views.common :as common]
            [jikken.datomic :as jida]
            [noir.content.getting-started])
  (:use [noir.core :only [defpage]]))


(defpage "/" []
         (common/layout
           [:div.query
            [:h1 "実験"]
            [:h2 "Pre-built rules:"]
            [:ul
             (map #(vector :li (str (first (first %)))) jida/rules)]
            [:div.repos.well
             [:p "Available repos: "
              [:a {:href "#"} "codox/codox.leiningen"]
              ", "
              [:a {:href "#"} "ragtime/ragtime.lein 0.3.2"]
              ]]
            [:textarea#query-text
             {:rows 3
              :placeholder "Your query"}]
            [:input#query-submit.btn.btn-large.btn-primary {:value "run" :type "submit"}]
            [:p#results]]))
