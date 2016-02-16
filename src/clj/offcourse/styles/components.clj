(ns offcourse.styles.components
  (:require [offcourse.styles.components.todo-list :refer [todo-list]]
            [offcourse.styles.components
             [collection-panel :refer [collection-panel collection-panels]]
             [label :refer [label]]
             [card :refer [card]]
             [cards :refer [cards]]
             [viewer :refer [viewer]]
             [dashboard :refer [dashboard]]]))

(defn components [config]
  (let [components [dashboard card viewer label cards todo-list collection-panels collection-panel]]
    (for [component components] (component config))))