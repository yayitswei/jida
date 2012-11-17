(ns jikken.views.welcome
  (:require [jikken.views.common :as common]
            [noir.content.getting-started])
  (:use [noir.core :only [defpage]]))

(defpage "/" []
         (common/layout
           [:p "Welcome to jikken"]
           [:textarea#query-text
            {:rows 3} "the query"]
           [:input#query-submit.btn {:value "run" :type "submit"}]
           [:p#results]))
