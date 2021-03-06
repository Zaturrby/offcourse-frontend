(ns offcourse.models.profile.index
  (:require [offcourse.protocols.queryable :as qa :refer [Queryable]]
            [offcourse.protocols.validatable :as va :refer [Validatable]]
            [cljs.spec :as spec]
            [offcourse.specs.base :as base]
            [cuerdas.core :as str]))

(defrecord Profile [user-name]
  Validatable
  (-valid? [{:keys [user-name] :as profile}]
    (spec/valid? ::base/user-profile profile))
  Queryable
  (-refresh [profile {:keys [user-name] :as query}]
    (assoc profile :user-name (keyword (str/slugify user-name)))))

(defn new [overrides] (map->Profile overrides))
