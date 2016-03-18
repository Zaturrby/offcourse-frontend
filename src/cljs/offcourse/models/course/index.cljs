(ns offcourse.models.course.index
  (:require [offcourse.models.checkpoint :refer [Checkpoint]]
            [offcourse.models.course.add :as add-impl]
            [offcourse.models.course.get :as get-impl]
            [offcourse.models.course.remove :as remove-impl]
            [offcourse.protocols.queryable :as qa :refer [Queryable]]
            [offcourse.protocols.validatable :as va :refer [Validatable]]
            [schema.core :as schema :include-macros true]
            [cuerdas.core :as str]))

(schema/defrecord Course
    [course-id    :- schema/Num
     base-id      :- schema/Num
     course-slug  :- schema/Str
     timestamp    :- schema/Int
     version      :- [schema/Num]
     revision     :- schema/Num
     curator      :- schema/Keyword
     goal         :- schema/Any
     flags        :- #{schema/Keyword}
     forked-from  :- (schema/maybe schema/Num)
     forks        :- #{schema/Num}
     checkpoints  :- [Checkpoint]]
  Queryable
  (-check [course] (schema/check Course course))
  (-get [course query] (get-impl/get course query))
  (-remove [course query] (remove-impl/remove course query))
  (-add [course query] (add-impl/add course query))
  Validatable
  (-valid? [{:keys [checkpoints] :as course}]
    (if (and (empty? (qa/check course)) (>= (count checkpoints) 1))
      true false)))

(defn complete [{:keys [goal] :as course}]
  (let [base-id (hash course)]
    (assoc course
           :base-id base-id
           :course-id base-id
           :revision 0
           :course-slug (str/slugify goal)
           :flags #{:featured}
           :forks #{}
           :timestamp (.now js/Date))))

(defn new
  ([defaults] (map->Course defaults))
  ([] (map->Course {})))
