(ns offcourse.system.interactions
  (:require [offcourse.protocols.queryable :as qa]
            [offcourse.protocols.responsive :as ri]
            [offcourse.protocols.renderable :as rr]
            [offcourse.protocols.authenticable :as ac]
            [offcourse.models.collection :as cl]))

(def actions
  {:api       [:not-found-data
               :found-data]
   :logger    [:updated-logs]
   :router    [:requested-route]
   :appstate  [:refreshed-state
               :checked-state
               :not-found-data]
   :auth      [:found-profile]
   :cloud     [:refreshed-credentials]
   :ui        [:rendered-view
               :requested-sign-in]})

(def reactions
  {:api      {:not-found-data qa/fetch}
   :logger   {:logged-action println}
   :router   {:refreshed-state       qa/refresh}
   :appstate {:requested-view qa/refresh}
   #_{:requested-view        qa/refresh
      :requested-update      qa/refresh
      :refreshed-credentials qa/refresh
      :requested-save        qa/add
      :found-data            qa/refresh
      :not-found-data        qa/check
      :found-profile         qa/refresh
      :not-found-profile     qa/refresh}
   :auth     {:requested-sign-in  ac/sign-in
              :requested-sign-out ac/sign-out}
   :cloud    {:signed-in-user    qa/refresh
              :signed-out-user   qa/reset
              :requested-save    qa/add
              :not-found-data    qa/fetch
              :requested-profile qa/get}
   :ui       {:refreshed-state rr/render}})
