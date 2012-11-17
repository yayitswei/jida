(ns jikken.views.welcome
  (:require [jikken.views.common :as common]
            [jikken.datomic :as jida]
            [noir.content.getting-started])
  (:use [noir.core :only [defpage]]))


(defpage "/" []
         (common/layout
           [:div.query
            [:h1 "実験"]
            [:ul
            (map #(vector :li (str (first (first %)))) jida/rules)]
            [:textarea#query-text
             {:rows 3
              :placeholder "Your query"}]
            [:input#query-submit.btn.btn-large.btn-primary {:value "run" :type "submit"}]
            [:p#results.well]]))
