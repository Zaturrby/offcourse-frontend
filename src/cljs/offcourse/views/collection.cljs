(ns offcourse.views.collection
  (:require [offcourse.views.components.dashboard :refer [dashboard]]
            [offcourse.views.components.card :refer [cards]]
            [offcourse.views.components.collection-panel :refer [collection-panels]]
            [rum.core :as rum]))

(rum/defc view [{:keys [view-name labels courses]} routes]
  [:.layout--app.app
   [:.layout--dashboard (dashboard (collection-panels labels routes) routes)]
   [:.layout--main (cards courses routes)]])