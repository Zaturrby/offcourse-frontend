(ns offcourse.appstate.check
  (:require [clojure.set :as set]
            [offcourse.protocols.queryable :as qa]
            [offcourse.protocols.responsive :refer [respond]]
            [offcourse.views.helpers :as vh]
            [offcourse.protocols.validatable :as va]))

(defn viewmodel-type [state]
  (-> state :viewmodel :type))

(defmulti check
  (fn [_ {:keys [type]}]type))

(defmethod check :permissions [{:keys [state] :as as} {:keys [proposal]}]
  (let [old-type (viewmodel-type @state)
        new-type(viewmodel-type proposal)
        user-name (-> proposal :user :user-name)]
    (cond
      (and (= new-type :new-course) (not user-name)) false
      (and (= old-type :new-user-view) (= new-type :new-user-view)) true
      (and (= old-type :new-user-view) (not user-name)) false
      :default true)))
