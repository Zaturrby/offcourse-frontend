(ns offcourse.viewmodels.collection-view.index
  (:require [medley.core :as medley]
            [offcourse.models.collection :refer [Collection]]
            [offcourse.models.course :refer [Course]]
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
  (valid? [vm] (if (qa/check vm) false true))
  Queryable
  (check [vm] (schema/check CollectionView vm))
  (refresh [vm store] #_(qa-impl/refresh vm store)))

(def graph
  {:view-name      (fnk [view-type] view-type)
   :collection     (fnk [datastore collection-data]
                        (or (qa/get datastore :collection collection-data)
                            collection-data))
   :labels         (fnk [datastore]
                        (medley/map-vals lb/collection->labels
                                         (qa/get datastore :collection-names :all)))
   :course-ids     (fnk [collection] (:course-ids collection))
   :courses        (fnk [datastore course-ids]
                        (qa/get datastore :courses course-ids))})

(def compose (graph/compile graph))

(defn new [{:keys [collection type]} datastore]
  (let [view-data (compose {:view-type type
                            :collection-data collection
                            :datastore datastore})]
    (map->CollectionView view-data)))
