(ns offcourse.protocols.responsive
  (:require [cljs.core.async :refer [<! close! >!]]
            [offcourse.models.action :as action])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(defprotocol Responsive
  (listen [this])
  (mute [this])
  (restart [this])
  (respond [this status] [this status payload] [this status type result]))

(defn payload [type result]
  {:type type
   type result})

(def counter (atom 0))

(defn -respond
  ([{:keys [output-channel log-channel channels component-name]} status payload]
   (let [output-channel (or output-channel (:output channels))
         log-channel (or log-channel (:log channels))
         response (action/new status component-name payload)]
     #_(when true
       (println "--RESPONSE-----")
       (println "SENDER" component-name)
       (println status)
       (println payload))
     (go
       (swap! counter inc)
       (>! output-channel response)
       (when log-channel (>! log-channel response)))))
  ([this status type result](-respond this status (payload type result))))

(defn -listener [{:keys [channels component-name reactions] :as this}]
  (go-loop []
    (let [{:keys [type source payload] :as action} (<! (:input channels))
          reaction (type reactions)]
      #_(when true #_(= component-name :ui)
        (println "--MESSAGE----")
        (println "RECIPIENT" component-name)
        (println "SENDER" source)
        (println type)
        (println payload))
      (when reaction
        (if (= reaction :forward)
          (respond this type payload)
        (reaction this payload)))
      (recur))))

(defn -listen [{:keys [channels component-name reactions] :as this}]
  (assoc this :listener (-listener this)))

(defn -mute [{:keys [channels] :as this}]
  (close! (:input channels))
  (-> this
      (dissoc :listener)))

