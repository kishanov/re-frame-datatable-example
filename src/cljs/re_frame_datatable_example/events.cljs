(ns re-frame-datatable-example.events
  (:require [re-frame.core :as re-frame]
            [re-frame-datatable-example.db :as db]
            [re-frame-datatable.core :as dt]))


(re-frame/reg-event-db
  ::initialize-db
  (fn [_ _]
    db/default-db))


(re-frame/reg-event-db
  ::set-active-label
  (fn [db [_ label-key]]
    (assoc db :active-label label-key)))


(re-frame/reg-event-db
  ::change-starred
  (fn [db [_ thread-id]]
    (update-in db [:email-threads thread-id :starred?] not)))


(re-frame/reg-event-db
  ::delete-forever
  (fn [db [_ thread-id]]
    (update-in db [:email-threads] dissoc thread-id)))



(re-frame/reg-event-fx
  ::set-thread-label
  (fn [cofx [_ thread-ids label-key dt-id]]
    {:db       (reduce (fn [ret-cofx thread-id]
                         (assoc-in ret-cofx [:email-threads thread-id :label] label-key))
                       (:db cofx)
                       thread-ids)
     :dispatch [::dt/unselect-all-rows dt-id]}))
