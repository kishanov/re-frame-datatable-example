(ns re-frame-datatable-example.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame]))



(re-frame/reg-sub
  ::email-threads
  (fn [db]
    (:email-threads db)))


(re-frame/reg-sub
  ::threads-digest
  :<- [::email-threads]
  (fn [email-threads]
    (->> email-threads
         (map (fn [t]
                {:participants       (map :from t)
                 :subject            (:subject (first t))
                 :last-received-date (:date (last t))})))))
