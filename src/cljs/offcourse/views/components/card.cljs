(ns offcourse.views.components.card
  (:require [offcourse.views.components.label :refer [labels]]
            [offcourse.views.components.item-list :refer [item-list]]
            [rum.core :as rum]))

(rum/defc card [{:keys [course-id goal tags description course-slug checkpoints curator] :as course}
                {:keys [course-url checkpoint-url] :as helpers}
                {:keys [toggle-checkpoint] :as handlers}]
  [:.container
   [:.card
    [:.card--frontside
     [:.card--section
      [:a.card--title {:href (course-url curator course-slug)} goal]
      [:.card--earmark ]]
     [:.card--section
      [:.card--rating
       [:.card--rating-dot {:data-dot-active "true"} ]
       [:.card--rating-dot {:data-dot-active "true"} ]
       [:.card--rating-dot ]
       [:.card--rating-dot ]
       [:.card--rating-dot ]]
      [:.label "Following"]
      [:img.card--picked {:src "http://imageshack.com/a/img923/3930/J18Srn.png"}]]
     [:.card--section (item-list :todo checkpoints
                                {:checkpoint-url (partial checkpoint-url curator course-slug)}
                                {:toggle-checkpoint (partial toggle-checkpoint course-id)}
                                (:trackable? (meta course)))]
     [:.card--section
       [:.label      "Start"]
       [:.card--info-corner "I"]]]
    ; [:.card--backside
    ;  [:.card--section
    ;   [:img.card--img {:src "http://placehold.it/150x150"}]
    ;   [:.card--meta
    ;    [:h6.card--smalltitle "John Diddididoe"]
    ;    [:h6.card--smalltitle "Expert"]]
    ;   [:.card--info-corner-back "I"]]
    ;  [:.card--section
    ;   [:.card--stats
    ;   [:span.card--smalltext "Posts: "]
    ;   [:span.card--smalltitle "10"]
    ;   [:span.card--smalltext " Learners: "]
    ;   [:span.card--smalltitle "40"]
    ;   [:span.card--smalltext " Forked: "]
    ;   [:span.card--smalltitle "5"]]]
    ;  [:.card--section (labels (:tags (meta course)) helpers)]]
     ]])

(rum/defc cards [items helpers handlers]
  [:.cards (map #(rum/with-key (card % helpers handlers) (:course-id %)) items)])
