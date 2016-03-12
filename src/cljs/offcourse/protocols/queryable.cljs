(ns offcourse.protocols.queryable
  (:refer-clojure :exclude [get]))

(defprotocol Queryable
  (-get     [this query])
  (-add     [this query])
  (-check   [this] [this query])
  (-fetch   [this query])
  (-refresh [this doc]))

(def payloads
  {:collection-names    :collection-type
   :collection          :collection
   :courses             :course-ids
   :course              :course
   :course-view         :dependencies
   :collection-view     :dependencies
   :checkpoint-view     :dependencies
   :checkpoint          :checkpoint
   :view                :view-data
   :url                 :checkpoint-slug
   :resources           :urls
   :resource            :resource})

(defn get
  ([this query] (-get this query))
  ([this type data] (get this {:type           type
                               (type payloads) data})))

(defn fetch [this query]
  (-fetch this query))

(defn refresh
  ([this query] (-refresh this query))
  ([this type data] (-refresh this {:type           type
                                    (type payloads) data})))

(defn add
  ([this query] (-add this query))
  ([this type data] (-add this {:type           type
                                (type payloads) data})))
(defn check
  ([this] (-check this))
  ([this query] (-check this query))
  ([this type data] (-check this {:type type
                                  (type payloads) data})))
