(ns slides.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(defonce app-state (atom {:text "Chestnut"}))

(defn main []
  (om/root
    (fn [app owner]
      (reify
        om/IRender
        (render [_]
          (dom/h1 nil (str "Hello " (:text app) "!")))))
    app-state
    {:target (. js/document (getElementById "app"))}))

;; (swap! app-state assoc :text "LambdaConf 2015")
