(ns offcourse.views.containers.app
  (:require[rum.core :as rum]))

(rum/defc app [{:keys [main menubar dashboard]}]
  [:.layout--app.app
   [:.layout--menubar menubar]
   [:.layout--main
    (when dashboard [:.layout--dashboard dashboard])
    [:.layout--content main]]])
