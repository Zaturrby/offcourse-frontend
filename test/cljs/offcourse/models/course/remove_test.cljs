(ns offcourse.models.course.remove-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [cuerdas.core :as str]
            [offcourse.models.course.index :as sut]
            [offcourse.protocols.queryable :as qa]))

(deftest models-course-delete
  (let [id              123
        missing-id      223
        goal            "alone in the dark"
        slug            (str/slugify goal)
        url             "http://offcourse.io"
        missing-url     "http://gibbon.co"
        buzzword        :agile
        user-id         :yeehaa
        checkpoint      {:checkpoint-id 1
                         :checkpoint-slug slug
                         :tags [buzzword]
                         :url url}]

    (testing "query type is checkpoint"
      (let [sut (-> (sut/new)
                    (assoc :checkpoints [checkpoint])
                    (qa/remove :checkpoint checkpoint))]
        (is (= (count (:checkpoints sut)) 0))))))

