(ns jikken.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page :only [include-css include-js html5]]))

(defn prod? []
  (get (System/getenv) "LEIN_NO_DEV"))

(defpartial layout [& content]
            (html5
              [:head
               [:title "Jida - Explore your Clojure projects"]
               (include-css "/css/bootstrap.simplex.min.css"
                            "/css/styles.css")]
              [:body
               [:div#wrapper.container
                content]
               (if (prod?)
                 (include-js
                   "/js/bin/main.js")
                 (include-js
                   "/js/bin-debug/main.js")
                 )]))
