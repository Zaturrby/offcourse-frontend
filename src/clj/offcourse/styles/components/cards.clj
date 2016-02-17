(ns offcourse.styles.components.cards
  (:refer-clojure :exclude [+ - * /])
  (:require [garden
             [arithmetic :refer [* +]]
             [stylesheet :refer [at-media]]]))

(defn cards [{:keys [templates breakpoints units]}]
  (let [{:keys [column-gap full column]} units]
    [[:.cards (merge (:component templates)
                     {:padding      [[full 0 0 0]]
                      :overflow-x   :hidden
                      :column-gap   column-gap})
      [:.container--card {:padding-bottom (:full units)}]]
    (for [{:keys [min-width max-width column-count]} breakpoints]
      (at-media {:min-width min-width :max-width max-width}
                (let [gap-count     (dec column-count)
                      actual-width (+ (* column-count column) (* gap-count column-gap))]
                  [:.cards {:display      (if (= column-count 0) :none :block)
                            :column-count column-count
                            :min-width    actual-width
                            :max-width    actual-width}])))]))
