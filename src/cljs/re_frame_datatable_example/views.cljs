(ns re-frame-datatable-example.views
  (:require [re-frame.core :as re-frame]
            [re-frame-datatable-example.subs :as subs]
            [re-frame-datatable-example.events :as events]
            [re-frame-datatable.core :as dt]
            [cljs.pprint :as pp]))


(defn main-table [dt-id data-sub-vector]
  [dt/datatable
   dt-id
   data-sub-vector
   [{::dt/column-key   [:starred?]
     ::dt/column-label " "
     ::dt/render-fn    (fn [starred? thread]
                         [:i.large.star.icon
                          {:on-click #(re-frame/dispatch [::events/change-starred (:id thread)])
                           :class    (if starred? "yellow" "grey empty")}])}

    {::dt/column-key   [:participants]
     ::dt/column-label "From"
     ::dt/render-fn    (fn [participants]
                         (let [names (->> participants
                                          (map (comp first #(clojure.string/split % #"@")))
                                          (distinct))]
                           [:span
                            (if (< 3 (count names))
                              (str (first names) " .. " (last names))
                              (clojure.string/join ", " names))

                            (when (< 1 (count participants))
                              [:span " (" (count participants) ")"])]))}

    {::dt/column-key   [:subject]
     ::dt/column-label "Subject"}

    {::dt/column-key   [:body-digest]
     ::dt/column-label "Digest"
     ::dt/render-fn    (fn [digest]
                         [:span.muted
                          {:style {:text-overflow "ellipsis"
                                   :max-width     "20em"}}
                          (->> digest
                               (take 40)
                               (apply str))])}

    {::dt/column-key   [:last-received-date]
     ::dt/column-label "Last received"
     ::dt/render-fn    (fn [val]
                         [:span
                          (.format (js/moment val) "YYYY-MM-DD")])
     ::dt/sorting      {::dt/enabled? true}}]

   {::dt/pagination    {::dt/enabled? true
                        ::dt/per-page 10}
    ::dt/selection     {::dt/enabled? true}
    ::dt/table-classes ["ui" "table"]}])



(defn labels-panel [active-label]
  (let [labels (re-frame/subscribe [::subs/labels])]
    [:div.ui.list
     (doall
       (for [{:keys [key title]} @labels]
         ^{:key key}
         [:div.item
          {:class    (when (= active-label key) "active")
           :on-click #(re-frame/dispatch [::events/set-active-label key])}
          title]))]))


(defn selected-threads-menu [dt-id data-sub-vector active-label]
  (let [selected (re-frame/subscribe [::dt/selected-items dt-id data-sub-vector])
        tooltip-common-attrs {:data-position "bottom center" :data-inverted true}]

    [:div
     (when-not (empty? @selected)
       [:div.ui.compact.icon.menu
        (when (= active-label :inbox)
          [:div
           (assoc tooltip-common-attrs :data-tooltip "Archive")
           [:a.icon.item
            {:on-click (fn []
                         (re-frame/dispatch [::events/set-thread-label (map :id @selected) :archived]))}
            [:i.archive.icon]]])

        (when (not= :spam active-label)
          [:div
           (assoc tooltip-common-attrs :data-tooltip "Report spam")
           [:a.icon.item
            {:on-click (fn []
                         (re-frame/dispatch [::events/set-thread-label (map :id @selected) :spam]))}
            [:i.warning.circle.icon]]])

        (when (not= :trash active-label)
          [:div
           (assoc tooltip-common-attrs :data-tooltip "Delete")
           [:a.icon.item
            {:on-click (fn []
                         (re-frame/dispatch [::events/set-thread-label (map :id @selected) :trash]))}
            [:i.trash.icon]]])])]))


(defn main-panel []
  (let [dt-id :email-threads
        data-sub-vector [::subs/threads-digest]
        active-label (re-frame/subscribe [::subs/active-label])]
    [:div.ui.container
     {:style {:margin-top "2em"}}
     [:div.ui.grid
      [:div.two.wide.column
       [labels-panel @active-label]]
      [:div.fourteen.wide.column
       [selected-threads-menu dt-id data-sub-vector @active-label]
       ^{:key @active-label}
       [main-table dt-id data-sub-vector]]]]))
