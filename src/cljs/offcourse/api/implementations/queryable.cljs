(ns offcourse.api.implementations.queryable
  (:require [cljs.core.async :refer [<!]]
            [clojure.set :as set]
            [offcourse.protocols.queryable :as qa]
            [cljs.core.match :refer-macros [match]]
            [offcourse.protocols.convertible :as ci]
            [offcourse.protocols.responsive :as ri])
(:require-macros [cljs.core.async.macros :refer [go]]))

(defn remove-from-end [s end]
  (if (.endsWith s end)
    (.substring s 0 (- (count s)
                       (count end)))
    s))

(defn singular [field]
  (-> field
      name
      (remove-from-end "s")
      keyword))

(defn missing [requested result field query]
  (let [field-singular (singular field)
        requested (into #{} (field query))
        found (into #{} (map (comp str field-singular) result))
        missing (set/difference requested found)]
    (if (empty? missing) false (into [] missing))))

(defn fetch-1 [service api {:keys [type] :as query} converter]
  (go
    (let [result (<! (qa/fetch (service api) query))]
      (if (:error result)
        (ri/respond api :not-found-data query)
        (ri/respond api :fetched-data type (converter result))))))

(defn fetch-m [service api {:keys [type] :as query} converter field]
  (go
    (let [result (<! (qa/fetch (service api) query))
          converted (map converter result)
          missing? (missing query converted field query)]
      (if missing?
        (ri/respond api :not-found-data (assoc query field missing?))
        (ri/respond api :fetched-data type converted)))))

(def fetch-cs-1 (partial fetch-1 :courses-service))
(def fetch-cs-m (partial fetch-m :courses-service))
(def fetch-rs-1 (partial fetch-1 :resources-service))
(def fetch-rs-m (partial fetch-m :resources-service))

(defmulti fetch (fn [_ {:keys [type]}] type))

(defmethod fetch :courses          [api query] (fetch-cs-m api query ci/to-course :course-ids))
(defmethod fetch :course           [api query] (fetch-cs-1 api query ci/to-course))
(defmethod fetch :collection       [api query] (fetch-cs-1 api query ci/to-collection))
(defmethod fetch :collection-names [api query] (fetch-cs-1 api query identity))
(defmethod fetch :resource         [api query] (fetch-rs-1 api query ci/to-resource))
(defmethod fetch :resources        [api query] (fetch-rs-m api query ci/to-resource :resource-ids))
