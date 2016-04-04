(ns offcourse.core
  (:require [offcourse.system.index :refer [system]]
            [offcourse.adapters.pouchdb.index :as pouchdb]
            cljsjs.aws-sdk-js
            [offcourse.adapters.fakedb.index :as fakedb]))


(defn app [bootstrap-docs auth-config cloud-config]
  (system "Offcourse_"
          [{:adapter           fakedb/new-db
                         :supported-types [:collection-names :collection
                                           :course :courses]
                         :name            :courses-repo}
                        {:adapter         fakedb/new-db
                         :supported-types [:resource :resources]
                         :name            :resources-repo}]
          auth-config
          cloud-config))
