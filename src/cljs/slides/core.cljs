(ns slides.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(defonce app-state (atom {:slide {}}))

(defn slide [model owner]
  (reify
    om/IRender
    (render [_]
      (dom/section #js {:className "slide"}
        (when-let [bg (:bg model)]
          (dom/img #js {:className "bg" :src bg}))
        (dom/div #js {:className "slide-content"}
           (dom/h1 #js {} (:title model)))))))

(defn app [model owner]
  (reify
        om/IRender
        (render [_]
          (dom/div #js {:className "content"}
                   (om/build slide (:slide model))))))

(def slide-img
  {:title "A Bird's Eye View of ClojureScript"
   :bg "/images/gull.jpg"})

(defn main []
  (om/root app app-state {:target (. js/document (getElementById "app"))}))

(swap! app-state assoc :slide slide-img)
