(ns re-frame-datatable-example.events
  (:require [re-frame.core :as re-frame]
            [re-frame-datatable-example.db :as db]))


(re-frame/reg-event-db
  ::initialize-db
  (fn [_ _]
    db/default-db))


(re-frame/reg-event-db
  ::set-active-label
  (fn [db [_ label-key]]
    (assoc db :active-label label-key)))
