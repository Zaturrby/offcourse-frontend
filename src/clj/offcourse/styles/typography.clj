(ns offcourse.styles.typography
  (:refer-clojure :exclude [+ - * /])
  (:require [garden
             [arithmetic :refer [* /]]
             [units :as u :refer [px]]
             [selectors :as s]
             [stylesheet :refer [at-font-face]]]))

(defn make-at-font-face [[font-name file-name]]
  (at-font-face {:font-family font-name
                 :font-weight 500
                 :src         (str "url('/fonts/" file-name ".woff') " "format('woff')")}))

(defn title [{:keys [fonts]}]
  [:.title {:font-family (:title fonts)}])


(defn textbar [{:keys [colors units fonts] :as config}]
  [:.textbar {:outline          :none
              :font-family      (:logo fonts)
              :display          :inline-block
              :font-size        (:subtitle-font units)
              :line-height      (:subtitle-line-height units)
              :padding          [[0 (:third units)]]
              :border           :none
              :background-color (:night colors)
              :color            (:day colors)}])

(defn typography [{:keys [fonts] :as config}]
  (let [components [title textbar]]
    [(map make-at-font-face (:raw fonts))
     (for [component components]
       (component config))]))