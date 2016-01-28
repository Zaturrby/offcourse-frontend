(ns offcourse.appstate.index
  (:require [com.stuartsierra.component :refer [Lifecycle]]
            [offcourse.appstate.composable :as ca-impl]
            [offcourse.appstate.lifecycle :as lc-impl]
            [offcourse.appstate.queryable :as qa-impl]
            [offcourse.protocols.composable :as ca :refer [Composable]]
            [offcourse.protocols.queryable :as qa :refer [Queryable]]
            [offcourse.protocols.responsive :as ri :refer [Responsive]]
            [schema.core :as schema]))

(schema/defrecord Appstate
    [component-name :- schema/Keyword
     channels       :- {}
     viewmodels     :- {}
     actions        :- []
     reactions      :- {}]
  Lifecycle
  (start [as] (lc-impl/start as))
  (stop [as] (lc-impl/stop as))
  Queryable
  (refresh [as query] (qa-impl/refresh as query))
  Responsive
  (respond [as status payload] (ri/-respond as status payload))
  (listen [as] (ri/-listen as))
  Composable
  (compose [as] (ca-impl/compose as))
  (compose [as query] (ca-impl/compose as query)))

(defn new []
  (map->Appstate {:component-name :appstate}))
