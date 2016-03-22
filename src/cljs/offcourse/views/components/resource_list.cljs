(ns offcourse.views.components.resource-list
  (:require [rum.core :as rum]
            [offcourse.views.components.label :refer [labels]]
            [bidi.bidi :refer [path-for]]))

(rum/defc resource-list-item [{:keys [title checkpoint-count tags url tasks] :as resource}
                              url-helpers
                              {:keys [update-appstate]}]
  [:.container
   [:li.resource-list--item
    [:.info
     [:h1 {:on-click #(update-appstate {:type :add-checkpoint
                                        :checkpoint {:url  url
                                                     :task (first tasks)
                                                     :tags tags}})} "+"]
     [:h1.title (first tasks)]
     [:p.resource_title title]
     [:p.url url]]
    [:.tags (labels (map (fn [tag] {:label-name tag}) tags)
                    url-helpers)]]])

(rum/defcs resource-form < (rum/local {}) [state {:keys [update-appstate]}]
  (let [local (:rum/local state)]
    [:.container
     [:li.resource-list--item
      [:.info {:key :info}
       [:h1 {:key :add-button
             :on-click #(update-appstate {:type :add-checkpoint
                                          :checkpoint {:url  "HEllO"
                                                       :task (:task @local)
                                                       :tags [:hi :BYE]}})} "+"]
       [:input.title {:key :title
                      :placeholder "Enter Unique Title Here"
                      :value (:task @local)
                      :on-change (fn [event]
                                   (swap! local #(assoc % :task (-> event .-target .-value))))}]
       [:p.resource_title {:key :resource-title} "loading.."]
       [:input.url {:key :url
                      :placeholder "Enter Unique Title Here"
                      :value (:url @local)
                      :on-change (fn [event]
                                   (swap! local #(assoc % :url (-> event .-target .-value))))}]]
      [:.tags {:key :tags
               :content-editable true
               :placeholder "tags"}]]]))

(rum/defc resource-list [resources url-helpers handlers]
  [:ul.resource-list
   (resource-form handlers)
   (map #(rum/with-key (resource-list-item % url-helpers handlers) (:url %))
        resources)])
