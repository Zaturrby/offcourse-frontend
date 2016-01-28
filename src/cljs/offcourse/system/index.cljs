(ns offcourse.system.index
  (:require [com.stuartsierra.component :as component]
            [offcourse.appstate.index :as appstate]
            [offcourse.datastore.index :as datastore]
            [offcourse.logger.index :as logger]
            [offcourse.router.index :as router]
            [offcourse.api.index :as repo]
            [offcourse.renderer.index :as renderer]
            [offcourse.system.interactions :refer [actions reactions]]
            [offcourse.system.routes :refer [routes]]
            [offcourse.system.plumbing :as plumbing]
            [offcourse.system.viewmodels :refer [viewmodels]]
            [offcourse.views.debug :as debug]
            [offcourse.protocols.convertible :as ci]))

(def courses-fetchables
  {:courses          [ci/to-course :course-ids]
   :course           [ci/to-course]
   :collection       [ci/to-collection]
   :collection-names [identity]})

(def resources-fetchables
  {:resources          [ci/to-resource :resource-ids]
   :resource           [ci/to-resource]})

(defn system [bootstrap-docs databases]
  (let [channels plumbing/channels]
    (component/system-map
     :courses-service        (:courses databases)
     :resources-service      (:resources databases)
     :routes                 routes
     :viewmodels             viewmodels
     :api-actions            (:api actions)
     :api-reactions          (:api reactions)
     :courses-fetchables     courses-fetchables
     :courses-channels       (:courses channels)
     :courses-fetchables     courses-fetchables
     :courses                (component/using (repo/new :courses)
                                             {:channels   :courses-channels
                                              :actions    :api-actions
                                              :reactions  :api-reactions
                                              :fetchables :courses-fetchables
                                              :service    :courses-service})
     :resources-channels     (:resources channels)
     :resources-fetchables   resources-fetchables
     :resources              (component/using (repo/new :resources)
                                              {:channels   :resources-channels
                                               :actions    :api-actions
                                               :reactions  :api-reactions
                                               :fetchables :resources-fetchables
                                               :service    :resources-service})
     :renderable             debug/debugger
     :router-actions         (:router actions)
     :router-reactions       (:router reactions)
     :router-channels        (:router channels)
     :router                 (component/using (router/new)
                                              {:channels  :router-channels
                                               :routes    :routes
                                               :actions   :router-actions
                                               :reactions :router-reactions})
     :logger-actions         (:logger actions)
     :logger-reactions       (:logger reactions)
     :logger-channels        (:logger channels)
     :logger                 (component/using (logger/new)
                                              {:channels  :logger-channels
                                               :actions   :logger-actions
                                               :reactions :logger-reactions})

     :datastore-actions      (:datastore actions)
     :datastore-reactions    (:datastore reactions)
     :datastore-channels     (:datastore channels)
     :datastore              (component/using (datastore/new)
                                              {:channels  :datastore-channels
                                               :actions   :datastore-actions
                                               :reactions :datastore-reactions})
     :appstate-actions       (:appstate actions)
     :appstate-reactions     (:appstate reactions)
     :appstate-channels      (:appstate channels)
     :appstate               (component/using (appstate/new)
                                              {:channels   :appstate-channels
                                               :viewmodels :viewmodels
                                               :actions    :appstate-actions
                                               :reactions  :appstate-reactions})
     :renderer-actions       (:renderer actions)
     :renderer-reactions     (:renderer reactions)
     :renderer-channels      (:renderer channels)
     :renderer               (component/using (renderer/new)
                                              {:channels       :renderer-channels
                                               :view-component :renderable
                                               :actions        :renderer-actions
                                               :reactions      :renderer-reactions}))))