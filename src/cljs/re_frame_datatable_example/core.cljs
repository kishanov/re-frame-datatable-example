(ns re-frame-datatable-example.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [re-frame-datatable-example.events :as events]
            [re-frame-datatable-example.subs]
            [re-frame-datatable-example.views :as views]
            [re-frame-datatable-example.config :as config]))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))


(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))


(defn ^:export init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
