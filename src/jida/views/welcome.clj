(ns jida.views.welcome
  (:require [jida.views.common :as common]
            [jida.datomic :as jida]
            [noir.content.getting-started])
  (:use [noir.core :only [defpage]]))


(defpage "/" []
         (common/layout
           [:div.query
            [:h1 "Jida - Explore Clojure Projects"]
            [:div.repos
             [:p "Available repos: "
              [:div#available-repos]]]
            [:div
             [:label "Add your repo"]
             [:input#repo-address.input-xlarge
              {:type "text"
               :value "git@github.com:yayitswei/jida.git"}]
             [:input#import-repo-btn.btn.btn-small
              {:value "import"
               :type "submit"}]
             [:div#import-status.alert.alert-info ""]]
            [:textarea#query-text
             {:rows 3
              :placeholder "Your query"}
             "[:find ?repo-names :where [?repos :repo/uri ?repo-names]]"]
            [:input#query-submit.btn.btn-large.btn-primary {:value "run" :type "submit"}]
            [:img#loader {:style "display:none;" :src "https://www.zenboxapp.com/assets/loading.gif"}]
            [:div#error-messages.alert {:style "display:none;"} "Your parens at offet(s) "
             [:span#error-offsets]
             " aren't properly balanced, please check again"]
            [:p#results]]))
