(ns re-frame-datatable-example.views
  (:require [re-frame.core :as re-frame]
            [re-frame-datatable-example.subs :as subs]
            [re-frame-datatable-example.events :as events]
            [re-frame-datatable.core :as dt]
            [cljs.pprint :as pp]))


(defn main-table []
  [dt/datatable
   :emails
   [::subs/threads-digest]
   [{::dt/column-key   [:starred?]
     ::dt/column-label " "
     ::dt/render-fn    (fn [starred?]
                         (if starred?
                           [:i.large.yellow.star.icon]
                           [:i.large.grey.empty.star.icon]))}

    {::dt/column-key   [:participants]
     ::dt/column-label "From"
     ::dt/render-fn    (fn [participants]
                         (let [names (map (comp first #(clojure.string/split % #"@"))
                                          participants)]
                           [:span
                            (if (< 3 (count names))
                              (str (first names) " .. " (last names))
                              (clojure.string/join ", " names))]))}

    {::dt/column-key   [:subject]
     ::dt/column-label "Subject"}

    {::dt/column-key   [:last-received-date]
     ::dt/column-label "Last received"
     ::dt/render-fn    (fn [val]
                         [:span
                          (.fromNow (js/moment val))])
     ::dt/sorting      {::dt/enabled? true}}]

   {::dt/pagination    {::dt/enabled? true
                        ::dt/per-page 10}
    ::dt/table-classes ["ui" "table"]}])



(defn labels-panel []
  (let [labels (re-frame/subscribe [::subs/labels])
        active-label (re-frame/subscribe [::subs/active-label])]
    [:div.ui.list
     (doall
       (for [{:keys [key title]} @labels]
         ^{:key key}
         [:div.item
          {:class    (when (= @active-label key) "active")
           :on-click #(re-frame/dispatch [::events/set-active-label key])}
          title]))]))



(defn main-panel []
  [:div.ui.container
   {:style {:margin-top "2em"}}
   [:div.ui.grid
    [:div.two.wide.column
     [labels-panel]]
    [:div.fourteen.wide.column
     [main-table]]]])
