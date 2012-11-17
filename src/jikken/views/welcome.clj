(ns jikken.views.welcome
  (:require [jikken.views.common :as common]
            [noir.content.getting-started])
  (:use [noir.core :only [defpage]]))

(defpage "/" []
         (common/layout
           [:p "Welcome to jikken"]))
