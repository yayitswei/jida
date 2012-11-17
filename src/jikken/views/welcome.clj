(ns jikken.views.welcome
  (:require [jikken.views.common :as common]
            [noir.content.getting-started])
  (:use [noir.core :only [defpage]]))

(defpage "/" []
         (common/layout
           [:div.query
            [:h1 "実験"]
            [:textarea#query-text
             {:rows 3
              :placeholder "Your query"}]
            [:input#query-submit.btn.btn-large.btn-primary {:value "run" :type "submit"}]
            [:p#results.well]]))
