(ns re-frame-datatable-example.views
  (:require [re-frame.core :as re-frame]
            [re-frame-datatable-example.subs :as subs]
            [re-frame-datatable.core :as dt]
            [cljs.pprint :as pp]))



(defn main-panel []
  [:div
   (with-out-str
     (pp/pprint @(re-frame/subscribe [::subs/threads-digest])))]

  [dt/datatable
   :emails
   [::subs/threads-digest]
   [{::dt/column-key   [:participants]
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

   {::dt/pagination {::dt/enabled? true
                     ::dt/per-page 10}}])
