(ns offcourse.system.handlers
  (:require [bidi.bidi :refer [path-for]]))

(def handlers
  {:sign-in (fn [responder] (partial responder :requested-sign-in :user))
   :update-appstate (fn [responder] (partial responder :requested-update :appstate))})
