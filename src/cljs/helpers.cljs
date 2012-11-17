(ns cljs.helpers
  (:require [domina :as d]))

(defn show [node-seq]
  "Sets the display style attr of a node-seq to block (visible)"
  (d/set-style! node-seq "display" "block"))

(defn hide [node-seq]
  "Sets the display style attr of a node-seq to none (invisible)"
  (d/set-style! node-seq "display" "none"))

(defn visible? [node-seq]
  (let [display (d/style node-seq "display")]
    (when (not (= display "none")) true)))

(defn toggle [node-seq]
  "Toggles the visibility of a node-seq using Domina"
  (if (visible? node-seq)
    (hide node-seq)
    (show node-seq)))
