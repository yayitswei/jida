(ns cljs.main
  (:refer-clojure :exclude [val empty remove find])
  (:require [domina :as d]
            [domina.events :as evt]
            [fetch.remotes :as remotes]
            [clojure.browser.repl :as repl])
  (:use-macros [crate.def-macros :only [defpartial]])
  (:require-macros [fetch.macros :as fm]))

(defn development? []
  (= document/domain "localhost"))
(def host (str "http://" document/domain))

(def sample-results
  [["(defn +\n  \"Returns the sum of nums. (+) returns 0. Does not
auto-promote\n  longs, will throw on overflow. See also: +'\"\n
{:inline (fn [x y] `(. clojure.lang.Numbers (add ~x ~y)))\n
:inline-arities #{2}\n   :added \"1.2\"}\n  ([] 0)\n  ([x] (cast
Number x))\n  ([x y] (. clojure.lang.Numbers (add x y)))\n  ([x y &
more]\n     (reduce1 + (+ x y) more)))" #inst
"2009-07-02T04:42:14.000-00:00"] ["(defn +\n  \"Returns the sum of
nums. (+) returns 0. Does not auto-promote\n  longs, will throw on
overflow. See also: +'\"\n  {:inline (fn [x y] `(.
clojure.lang.Numbers (~(if *unchecked-math* 'unchecked_add 'add) ~x
~y)))\n   :inline-arities #{2}\n   :added \"1.2\"}\n  ([] 0)\n  ([x]
(cast Number x))\n  ([x y] (. clojure.lang.Numbers (add x y)))\n  ([x
y & more]\n     (reduce1 + (+ x y) more)))" #inst
"2010-01-28T05:18:09.000-00:00"] ["(defn +\n  \"Returns the sum of
nums. (+) returns 0. Does not auto-promote\n  longs, will throw on
overflow. See also: +'\"\n  {:inline (nary-inline 'add
'unchecked_add)\n   :inline-arities >1?\n   :added \"1.2\"}\n  ([]
0)\n  ([x] (cast Number x))\n  ([x y] (. clojure.lang.Numbers (add x
y)))\n  ([x y & more]\n     (reduce1 + (+ x y) more)))" #inst
"2010-06-01T19:42:31.000-00:00"] ["(defn +\n  \"Returns the sum of
nums. (+) returns 0.\"\n  {:inline (fn [x y] `(. clojure.lang.Numbers
(add ~x ~y)))\n   :inline-arities #{2}\n   :added \"1.0\"}\n  ([] 0)\n
 ([x] (cast Number x))\n  ([x y] (. clojure.lang.Numbers (add x y)))\n
 ([x y & more]\n   (reduce + (+ x y) more)))" #inst
"2010-01-30T03:32:43.000-00:00"] ["(defn +\n  \"Returns the sum of
nums. (+) returns 0.\"\n  {:inline (fn [x y] `(. clojure.lang.Numbers
(add ~x ~y)))\n   :inline-arities #{2}\n   :added \"1.0\"}\n  ([] 0)\n
 ([x] (cast Number x))\n  ([x y] (. clojure.lang.Numbers (add x y)))\n
 ([x y & more]\n   (reduce1 + (+ x y) more)))" #inst
"2010-06-11T17:40:44.000-00:00"] ["(defn +\n  \"Returns the sum of
nums. (+) returns 0.\"\n  {:inline (fn [x y] `(. clojure.lang.Numbers
(add ~x ~y)))\n   :inline-arities #{2}}\n  ([] 0)\n  ([x] (cast Number
x))\n  ([x y] (. clojure.lang.Numbers (add x y)))\n  ([x y & more]\n
(reduce + (+ x y) more)))" #inst "2009-10-24T12:39:27.000-00:00"]
["(defn +\n  \"Returns the sum of nums. (+) returns 0.\"\n  {:inline
(fn [x y] `(. clojure.lang.Numbers (addP ~x ~y)))\n   :inline-arities
#{2}\n   :added \"1.0\"}\n  ([] 0)\n  ([x] (cast Number x))\n  ([x y]
(. clojure.lang.Numbers (addP x y)))\n  ([x y & more]\n   (reduce1 +
(+ x y) more)))" #inst "2010-06-18T20:20:32.000-00:00"]])

(defpartial item [[text date]]
            [:div.result-item.alert
             [:h4.alert-heading date]
             [:p text]])

(defpartial items [items]
            [:div.results (map item items)])

(defn display-results [results]
    (d/set-html! (d/by-id "results") (items results))
    (d/set-style! (d/by-id "results") "display" "block"))

(defn submit-query [_]
  (fm/remote
    (query-codeq (d/value (d/by-id "query-text"))) [results]
    (display-results results)))

(defn ^:export setup []
  (evt/listen! (d/by-id "query-submit") :click submit-query)
;  (display-results sample-results)
  (when (development?)
    (d/log "Running in dev mode. Connecting to repl")
    (repl/connect (str host ":9000/repl"))))

(set! (.-onload js/window) setup)
