(ns cljs.test
  (:require [domina.css :as css]))

(defn testfn []
  (css/sel ".test"))

(defn testfn2 []
  (css/sel ".hello"))
