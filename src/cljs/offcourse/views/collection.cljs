(ns offcourse.views.collection
  (:require [offcourse.protocols.decoratable :as dc]
            [offcourse.protocols.queryable :as qa]
            [plumbing.core :refer-macros [fnk]]
            [clojure.set :as set]))

(defn course-tags [course]
  (->> course
       :checkpoints
       (map :tags)
       (apply set/union)
       (into #{})))

(defn filter-courses [{:keys [collection-name collection-type]} courses]
  (case collection-type
    :curators (filter (fn [course] (= collection-name (:curator course))) courses)
    :flags (filter (fn [course] (set/superset? (into #{} (:flags course)) #{collection-name})) courses)
    :tags (filter (fn [course] (set/superset? (course-tags course) #{collection-name})) courses)
    courses))

(def graph
  {:collection (fnk [appstate] (get-in appstate [:viewmodel :collection]))
   :courses         (fnk [appstate user-name collection]
                         (->> (:courses appstate)
                              (map #(dc/decorate %1 user-name nil))
                              (filter-courses collection)))
   :actions   (fnk [user-name [:url-helpers home-url new-course-url]]
                   {:add-course (when user-name (new-course-url user-name))})
   :main            (fnk [courses url-helpers handlers [:components cards]]
                         (cards courses url-helpers handlers))})
