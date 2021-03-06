(ns offcourse.fake-data.index
  (:require [clojure.string :as str]
            [faker.lorem :as lorem]
            #?(:cljs [cljs-uuid-utils.core :as uuid])
            [offcourse.fake-data.buzzwords :as bw]
            [offcourse.fake-data.courses :refer [raw-courses]]))

(def urls ["facebook.com"
           "google.com"
           "lifely.nl"
           "zeef.com"
           "yahoo.com"
           "pinterest.com"
           "gibbon.co"
           "offcourse.io"])

(def users ["yeehaa" "greg" "r2j2" "reika" "charlotte" "marijn"])

(defn rand-int-between [min max]
  (let [dev (- max min)]
    (+ (rand-int dev) min)))

(defn make-description []
  (->> (lorem/words)
       (take (rand-int-between 10 40))
       (map #(str/capitalize %1))
       (str/join " ")))

(defn to-snake-case [str]
  (as-> str %
    (str/lower-case %)
    (str/split % #" ")
    (str/join "-" %)))

(def buzzwords (map to-snake-case bw/buzzwords))
(def hashtags (atom (shuffle buzzwords)))

(defn set-of-buzzwords [min max]
  (->> buzzwords
       shuffle
       (take (rand-int-between min max))
       (into #{})))

(def flags [:featured :new :popular])

(defn generate-flags []
  (->> flags
       shuffle
       (take (rand-int 4))
       (into #{})))

(defn- index-checkpoint [index {:keys [task completed]}]
  {:checkpoint-id index
   :order index
   :task task
   :url (rand-nth urls)
   :tags (set-of-buzzwords 0 5)})

(defn- index-checkpoints [checkpoints]
  (->> checkpoints
       (map-indexed #(index-checkpoint %1 %2))))

(defn add-ids [course]
  (let [base-id (hash course)]
    (merge course {:base-id base-id
                   :course-id base-id})))

(defn hashtag []
  (let [hashtag (peek @hashtags)]
    (swap! hashtags pop)
    hashtag))

(defn generate-course
  ([] (generate-course (rand-nth users) (hashtag)))
  ([curator hashtag] (-> raw-courses
                         rand-nth
                         (assoc :version [0 0 0]
                                :revision 0
                                :hashtag hashtag
                                :description (make-description)
                                :timestamp (.now js/Date)
                                :forked-from nil
                                :forks #{}
                                :curator curator
                                :flags (generate-flags))
                         (update-in [:checkpoints] index-checkpoints)
                         add-ids)))
