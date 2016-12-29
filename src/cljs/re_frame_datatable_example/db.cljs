(ns re-frame-datatable-example.db
  (:require [re-frame-datatable-example.model :as model]))


(def default-db
  {:email-threads model/sample-inbox
   :labels        model/labels
   :active-label  (:key (first model/labels))})
