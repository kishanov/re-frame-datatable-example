(ns re-frame-datatable-example.views
  (:require [re-frame.core :as re-frame]
            [re-frame-datatable-example.subs :as subs]
            [re-frame-datatable-example.events :as events]
            [re-frame-datatable.core :as dt]))



(defn main-table [dt-id data-sub-vector active-label]
  [dt/datatable
   dt-id
   data-sub-vector
   [(if (= active-label :trash)
      {::dt/column-key   [:id]
       ::dt/column-label " "
       ::dt/render-fn    (fn [id]
                           [:i.large.grey.trash.icon
                            {:on-click #(re-frame/dispatch [::events/delete-forever id])
                             :style    {:cursor "pointer"}}])}

      {::dt/column-key   [:starred?]
       ::dt/column-label " "
       ::dt/render-fn    (fn [starred? thread]
                           [:i.large.star.icon
                            {:on-click #(re-frame/dispatch [::events/change-starred (:id thread)])
                             :class    (if starred? "yellow" "grey empty")}])})

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

   {::dt/pagination            {::dt/enabled? true
                                ::dt/per-page 10}
    ::dt/selection             {::dt/enabled? true}
    ::dt/table-classes         ["ui" "table"]
    ::dt/empty-tbody-component (fn []
                                 [:span "No starred messages. Stars let you give messages a special status to make them easier to find. To star a message, click on the star outline beside any message or conversation."])
    ::dt/footer-component      (fn []
                                 [:tr
                                  [:th {:col-span 6}
                                   [:strong "Total selected: " (count @(re-frame/subscribe [::dt/selected-items dt-id data-sub-vector]))]]])
    ::dt/tr-class-fn           (fn [thread]
                                 (let [selected-ids (->> @(re-frame/subscribe [::dt/selected-items dt-id data-sub-vector])
                                                         (map :id)
                                                         (set))]
                                   [(when (selected-ids (:id thread))
                                      "warning")]))}])



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



(defn menu-item [tooltip icon-class email-label selected dt-id]
  (let [tooltip-common-attrs {:data-position "bottom center" :data-inverted true}]
    [:div
     (assoc tooltip-common-attrs :data-tooltip tooltip)
     [:a.icon.item
      {:on-click (fn []
                   (re-frame/dispatch [::events/set-thread-label (map :id selected) email-label dt-id]))}
      [:i.icon
       {:class icon-class}]]]))



(defn selected-threads-menu [dt-id data-sub-vector active-label]
  (let [selected (re-frame/subscribe [::dt/selected-items dt-id data-sub-vector])]
    [:div
     (when-not (empty? @selected)
       [:div.ui.compact.icon.menu
        (when (= active-label :inbox)
          [menu-item "Archive" "archive" :archived @selected dt-id])

        (when (not= :spam active-label)
          [menu-item "Report spam" "warning circle" :spam @selected dt-id])

        (when (not= :trash active-label)
          [menu-item "Delete" "trash" :trash @selected dt-id])])]))



(defn gmail-like-pagination [db-id data-sub]
  (let [pagination-state (re-frame/subscribe [::re-frame-datatable.core/pagination-state db-id data-sub])]
    (fn []
      (let [{:keys [::re-frame-datatable.core/cur-page ::re-frame-datatable.core/pages]} @pagination-state
            total-pages (count pages)
            next-enabled? (< cur-page (dec total-pages))
            prev-enabled? (pos? cur-page)]

        [:div
         [:div {:style {:display      "inline-block"
                        :margin-right ".5em"}}
          [:strong
           (str (inc (first (get pages cur-page))) "-" (inc (second (get pages cur-page))))]
          [:span " of "]
          [:strong (inc (second (last pages)))]]

         [:div.ui.pagination.mini.menu
          [:a.item
           {:on-click #(when prev-enabled?
                         (re-frame/dispatch [::re-frame-datatable.core/select-prev-page db-id @pagination-state]))
            :class    (when-not prev-enabled? "disabled")}
           [:i.left.chevron.icon]]

          [:a.item
           {:on-click #(when next-enabled?
                         (re-frame/dispatch [::re-frame-datatable.core/select-next-page db-id @pagination-state]))
            :class    (when-not next-enabled? "disabled")}
           [:i.right.chevron.icon]]]]))))



(defn main-panel []
  (let [dt-id :email-threads
        data-sub-vector [::subs/threads-digest]
        active-label (re-frame/subscribe [::subs/active-label])]

    [:div.ui.container
     {:style {:margin-top "2em"}}

     [:h1.ui.header {:style {:padding 0}}
      "re-frame-datatable Example App"
      [:div.ui.right.floated.main.menu
       [:a.item {:href "https://github.com/kishanov/re-frame-datatable-example"}
        [:i.github.icon]]]]

     [:hr {:style {:margin-bottom "2em"}}]

     [:div.ui.grid
      [:div.row
       [:div.two.wide.column
        [labels-panel @active-label]]


       [:div.fourteen.wide.column
        [:div.ui.two.column.grid
         [:div.row
          [:div.column {:style {:padding-left 0}}
           [selected-threads-menu dt-id data-sub-vector @active-label]]
          [:div.right.aligned.column {:style {:padding-right 0}}
           [gmail-like-pagination dt-id data-sub-vector]]]

         [:div.two.column.row
          ^{:key @active-label}
          [main-table dt-id data-sub-vector @active-label]]]]]]]))
