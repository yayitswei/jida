(ns jikken.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page :only [include-css include-js html5]]))

(defpartial layout [& content]
            (html5
              [:head
               [:title "jikken"]
               (include-css "/css/bootstrap.min.css"
                            "/css/styles.css")]
              [:body
               [:div#wrapper.container
                content]
               (include-js
                   "/js/bin-debug/main.js")]))
