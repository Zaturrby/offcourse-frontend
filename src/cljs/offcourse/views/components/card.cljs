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
      [:.card--following "Following"]
      [:img.card--picked {:src "http://imageshack.com/a/img923/1755/lDeG76.png"}]]
     [:.card--section (item-list :todo checkpoints
                                {:checkpoint-url (partial checkpoint-url curator course-slug)}
                                {:toggle-checkpoint (partial toggle-checkpoint course-id)}
                                (:trackable? (meta course)))]
     [:.card--section
       [:.card--button      "Start"]
       [:.card--info-corner "I"]]]
    [:.card--backside
     [:.card--section
      [:a.card--title {:href (course-url curator course-slug)} goal]
      [:.card--earmark ]]
     [:.card--section
      [:img.card--img {:src "http://placehold.it/150x150"}]
      [:.card--meta
       [:.div
        [:h6.card--author "John Diddididoe"]
        [:h6.card--author-status "Expert"]]
       [:.card--stats
        [:span.card--stats-title "Posts: "]
        [:span.card--stats-num "10"]
        [:span.card--stats-title " Learners: "]
        [:span.card--stats-num "40"]
        ; [:span.card--stats-title " Forked: "]
        ; [:span.card--stats-num "5"]
        ]]]
     [:.card--section 
      [:p.card--text "Leverage agile frameworks to provide a robust synopsis for high level overviews. Iterative approaches to corporate."]]
      
     [:.card--section (labels (:tags (meta course)) helpers)]]
     ]])

(rum/defc cards [items helpers handlers]
  [:.cards (map #(rum/with-key (card % helpers handlers) (:course-id %)) items)])
