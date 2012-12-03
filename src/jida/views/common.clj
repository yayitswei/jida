(ns jida.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page :only [include-css include-js html5]]))

(def prod? (System/getenv "LEIN_NO_DEV"))

(def asset-host (if prod?
                  "http://jida.s3.amazonaws.com"
                  nil))

(def main-js
  (if prod? "/js/bin/main.js" "/js/bin-debug/main.js"))

(defn asset [file]
  (str asset-host file))

(defpartial layout [& content]
            (html5
              [:head
               [:title "Jida - Explore your Clojure projects"]
               (include-css "/css/bootstrap.simplex.min.css"
                            "/css/styles.css")]
              [:body
               [:div#wrapper.container
                content]
               (include-js
                 "https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"
                 "//netdna.bootstrapcdn.com/twitter-bootstrap/2.2.1/js/bootstrap.min.js"
                 "/js/init.js"
                 (asset main-js))
               ]))
