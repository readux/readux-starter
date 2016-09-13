(ns {{namespace}}.core
  (:require [reagent.core :as r]
            [reagent.ratom :refer [reaction]]
            [readux.core :as rdc :include-macros true]
            [readux.store :as rds]
            [readux.middleware.log-model-diff :refer [log-model-diff]]))

(enable-console-print!)

;; reducers
;; --------
(def app-reducer
  (rdc/reducer-fn
    [model action]
    :incr (update model :counter inc)
    :decr (update model :counter dec)))

;; store
;; -----
(defonce store (rdc/store app-reducer (rds/apply-mw log-model-diff)))

;; queries
;; -------
(defn- counter-value-query
  [model [query-id]]
  (assert (= query-id :counter-value))
  (reaction (:counter @model)))

;; presentational components
;; -------------------------
(defn counter
  [value on-inc on-dec]
  [:div
    [:p @value]
    [:button {:on-click on-inc} "+"]
    [:button {:on-click on-dec} "-"]])

;; control components
;; ------------------
(defn- app
  [store]
  (let [counter-value (rdc/query store [:counter-value])
        on-inc #(rdc/dispatch store {:type :inc})
        on-dec #(rdc/dispatch store {:type :dec})]
    (fn app-render []
      [:div
      [counter counter-value on-inc on-dec]])))

(defn page []
  (rdc/query-reg! store :counter-value counter-value-query)
  (fn page-render []
    [:div.container
     [:div.jumbotron
      [:h1 "{{name}}"]
      [:p "Welcome to {{name}}"]
      [app store]]]))

(r/render-component [page]
  (. js/document (getElementById "app")))