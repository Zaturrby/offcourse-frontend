(ns adapters.pouchdb.implementations.queryable
  (:require [cljs.core.async :refer [<!]]
            [adapters.pouchdb.wrapper :as wrapper]
            [cljs.core.match :refer-macros [match]]
            [offcourse.protocols.queryable :as qa])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn- check-doc [connection query]
  (go
    (let [error (:error (<! (qa/fetch connection query)))]
      (if error false true))))

(defn- check-docs [connection query]
  (go
    (let [docs         (<! (qa/fetch connection query))
          are-missing? (filter :error docs)
          are-deleted? (filter (comp :deleted :value) docs)
          missing-docs (map :id (concat are-missing? are-deleted?))]
      (or (empty? missing-docs) missing-docs))))

(defn- query [connection options cb viewname]
  (let [viewname (str "query/" (name viewname))]
    (wrapper/query connection viewname options cb)))

(defn- fetch-docs [connection {:keys [doc-ids]}]
  (let [options {:keys doc-ids
                 :include-docs true}
        cb      (partial map :doc)]
    (wrapper/all-docs connection options cb)))

(defn- fetch-courses [connection course-ids cb]
  (wrapper/query connection "query/courseIds"
                 {:keys course-ids
                  :reduce false
                  :include_docs true}
                 (comp doall cb (partial map :doc))))

(defn- fetch-collection [connection {:keys [collection-type collection-name] :as collection}]
  (go
    (let [options {:reduce false :key collection-name}
          cb      (partial into #{} (map :value))
          result  (<! (query connection options cb collection-type))]
      (assoc collection :collection-ids result))))

(defn- fetch-collection-names [connection]
  (let [options       {:group true}
        cb            (partial into #{} (map #(keyword (:key %1))))
        extract-names (partial query connection options cb)]
  (go
    {:flags    (<! (extract-names :flags))
     :tags     (<! (extract-names :tags))
     :curators (<! (extract-names :curators))})))

(defn fetch [{:keys [connection]} query]
  (match [query]
         [{:type :collection-names}] (fetch-collection-names connection)
         [{:collection collection}]  (fetch-collection connection collection)
         [{:courses course-ids}]     (fetch-courses connection course-ids identity)
         [{:course course}]          (fetch-courses connection
                                                    [(:course-id course)]
                                                    first)
         [{:key key}]                (wrapper/get-doc connection key)
         [{:keys _}]                 (wrapper/all-docs connection query)))

(defn refresh [{:keys [connection]} query]
  (match [query]
         [{:doc doc}]   (wrapper/put-doc connection doc)
         [{:docs docs}] (wrapper/bulk-docs connection docs)))

(defn check [connection query]
  (match [query]
         [{:key key}]   (check-doc connection query)
         [{:keys keys}] (check-docs connection query)))
