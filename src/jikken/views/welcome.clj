(ns jikken.views.welcome
  (:require [jikken.views.common :as common]
            [jikken.datomic :as jida]
            [noir.content.getting-started])
  (:use [noir.core :only [defpage]]))


(defpage "/" []
         (common/layout
           [:p "Welcome to jikken"]
           [:ul
            (map #(vector :li (str (first (first %)))) jida/rules)]
           [:textarea#query-text
            {:rows 3} "the query"]
           [:input#query-submit.btn {:value "run" :type "submit"}]
           [:p#results]))
