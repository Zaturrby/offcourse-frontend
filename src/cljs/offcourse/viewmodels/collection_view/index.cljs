(ns offcourse.viewmodels.collection-view.index
  (:require [medley.core :as medley]
            [offcourse.models.collection :refer [Collection]]
            [offcourse.models.course :as co :refer [Course]]
            [offcourse.models.datastore.index :as ds]
            [offcourse.models.label :as lb :refer [Label]]
            [offcourse.protocols.queryable :as qa :refer [Queryable]]
            [offcourse.protocols.validatable :as va :refer [Validatable]]
            [offcourse.viewmodels.collection-view.validatable :as va-impl]
            [plumbing.core :refer-macros [fnk]]
            [plumbing.graph :as graph]
            [schema.core :as schema :include-macros true]))

(schema/defrecord CollectionView
    [view-name  :- Keyword
     labels     :- {Keyword #{Label}}
     collection :- Collection
     courses    :- (schema/conditional #(not (nil? %)) [Course])]
  Validatable
  (missing-data [vm] (va-impl/missing-data vm))
  (-valid? [vm] (if (qa/check vm) false true))
  Queryable
  (-check [vm] (schema/check CollectionView vm))
  (-refresh [vm store] #_(qa-impl/refresh vm store)))

(defn augment-course [selected {:keys [checkpoints] :as course}]
  (let [tags (-> (co/get-tags course)
                 (lb/collection->labels selected))]
    (-> course (with-meta {:tags tags}))))

(def graph
  {:view-data       (fnk [appstate] (:view appstate))
   :view-name       (fnk [view-data] (:type view-data))
   :collection-data (fnk [view-data] (:collection view-data))
   :collection      (fnk [datastore collection-data]
                         (or (qa/get datastore :collection collection-data)
                             collection-data))
   :labels          (fnk [collection datastore]
                         (->> (qa/get datastore :collection-names :all)
                              (medley/map-vals
                               (fn [category] (into #{} (map #(lb/new % (:collection-name collection)) category))))))
   :course-ids      (fnk [collection] (:course-ids collection))
   :courses         (fnk [datastore course-ids collection]
                         (some->> (qa/get datastore :courses course-ids)
                                  (map (partial augment-course
                                                (:collection-name collection)))))})

(def compose (graph/compile graph))

(defn dummy [collection] {:type :collection-view
                          :collection collection})

(defn new [appstate datastore]
  (-> {:appstate appstate
       :datastore (or datastore (ds/new))}
      compose
      map->CollectionView))
