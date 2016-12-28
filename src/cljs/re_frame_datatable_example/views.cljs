(ns re-frame-datatable-example.views
  (:require [re-frame.core :as re-frame]
            [re-frame-datatable-example.subs :as subs]))


(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    (fn []
      [:div "Hello from " @name])))
