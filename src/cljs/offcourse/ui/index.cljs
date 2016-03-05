(ns offcourse.ui.index
  (:require [com.stuartsierra.component :refer [Lifecycle]]
            [medley.core :as medley]
            [offcourse.models.view :as view]
            [offcourse.protocols.composable :as ca]
            [offcourse.protocols.mountable :as ma]
            [offcourse.protocols.renderable :as rr :refer [Renderable]]
            [offcourse.protocols.responsive :as ri :refer [Responsive]]
            [offcourse.protocols.validatable :as va]
            [schema.core :as schema]))

(defn augment-handler [component status handler]
  (let [base-handler (partial ri/respond component)]
    [status (partial handler base-handler)]))

(schema/defrecord UI
    [component-name :- schema/Keyword
     channels       :- {}
     url-helpers    :- {}
     handlers       :- {}
     views          :- {}
     viewmodels     :- {}
     actions        :- []
     reactions      :- {}]
  Lifecycle
  (start [rd] (ri/listen rd))
  (stop [rd] (ri/mute rd))
  Renderable
  (-render [{:keys [views url-helpers appstate components] :as rd} _]
    (let [view (view/new @appstate components url-helpers)]
      (-> view
          (ca/compose views)
          (rr/render)
          (ma/mount "#container"))
    (ri/respond rd :rendered-view)))

  Responsive
  (-listen [rd] (assoc rd :listener (ri/listen rd)))
  (-mute [rd] (dissoc rd :listener))
  (-respond [rd status] (ri/respond rd status nil))
  (-respond [rd status payload] (ri/respond rd status payload)))

(defn new []
  (map->UI {:component-name :ui}))
