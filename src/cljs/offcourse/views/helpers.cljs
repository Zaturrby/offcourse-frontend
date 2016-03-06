(ns offcourse.views.helpers)

(defn collection-view [collection]
  {:view-type    :collection-view
   :view-data    {:type       :collection
                  :collection collection}})

(defn checkpoint-view [{:keys [checkpoint-id] :as data}]
  {:view-type :checkpoint-view
   :view-data {:type       :course
               :course     (select-keys data [:curator :hashtag])
               :checkpoint (or (int checkpoint-id) 0)}})

(defn new-course-view [{:keys [curator]}]
  {:view-type :new-course-view
   :view-data {:type       :course
               :course     {:course-id :new
                            :curator curator}}})
