(ns offcourse.api.index
  (:require [cljs.core.async :refer [<! >! put! close!]]
            [com.stuartsierra.component :as component]
            [offcourse.adapters.pouchdb :as pouchdb])
  (:require-macros [cljs.core.async.macros :refer [go-loop go]]))

(defn listen [{:keys [output-channel input-channel]}]
  #_(put! output-channel {:status :api-ready})
  (go-loop []
    (let [action (<! input-channel)]
      (>! output-channel action)
      (recur))))

(defn bootstrap [{:keys [output-channel input-channel status service] :as api}]
  (go
    (let [{:keys [error] :as response} (<! (pouchdb/bootstrap service))]
      (println response)
      (if error
        (>! output-channel {:response response})
        (listen api)))))

(defrecord API [service output-channel input-channel]
  component/Lifecycle
  (start [api]
    (assoc api :listener (bootstrap api)))
  (stop [api]
    (close! input-channel)
    (dissoc api :listener)))

(defn new-api []
  (map->API {}))