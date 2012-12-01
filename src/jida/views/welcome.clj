(ns jida.views.welcome
  (:require [jida.views.common :as common]
            [jida.datomic :as jida]
            [noir.content.getting-started])
  (:use [noir.core :only [defpage]]))


(defpage "/" []
         (common/layout
           [:div.query
            [:h1 "Jida - Explore Clojure Projects"]
            [:h2 "Getting started"]
            [:ul.schema
             [:li
              [:a {:target "_blank" :href "http://cloud.github.com/downloads/Datomic/codeq/codeq.pdf"} "Codeq schema"]]
             [:li
              [:a {:target "_blank" :href "http://docs.datomic.com/tutorial.html"} "Datomic query tutorial"]]
             [:li
              [:a {:target "_blank" :href "https://github.com/devn/codeq-playground/blob/master/src/com/thinkslate/codeq_playground/core.clj"} "Useful example queries"]]
             [:li
              [:a {:target "_blank" :href "https://github.com/yayitswei/jida"} "Jida Source"]]]
            [:div.repos
             [:p "Available repos: "
              [:div#available-repos]]]
            [:div
             [:label "Add your repo"]
             [:input#repo-address.input-xlarge
              {:type "text"
               :value "https://github.com/clojure/clojure.git"}]
             [:input#import-repo-btn.btn.btn-small
              {:value "import"
               :type "submit"}]
             [:div#import-status.alert.alert-info ""]]
            [:div
             [:label "Recent saved queries"]
             [:div#query-history]]
            [:div
             [:strong#query-title]
             [:span#description]]
            [:textarea#query-text
             {:rows 3
              :placeholder "Your query"}
             "[:find ?repo-names :where [?repos :repo/uri ?repo-names]]"]
            [:input#query-submit.btn.btn-large.btn-primary {:value "run" :type "submit"}]
            [:span " | "]
            [:input#query-save.btn.btn-large.btn-safe {:value "save" :type "submit"}]
            [:img#loader {:style "display:none;" :src "https://www.zenboxapp.com/assets/loading.gif"}]
            [:div#error-messages.alert {:style "display:none;"} ]
            [:p#results]]))
