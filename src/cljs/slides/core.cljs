(ns slides.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(defonce app-state (atom {:slides []}))

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
      (apply dom/div #js {:className "content"}
               (om/build-all slide (:slides model))))))

(def slide-imgs
  [{:title "A Bird's Eye View of ClojureScript"
    :bg "/images/gull.jpg"}
   {:title "Using Om Components"
    :bg "/images/gull2.jpg"}])

(defn main []
  (om/root app app-state {:target (. js/document (getElementById "app"))}))

(swap! app-state assoc :slides slide-imgs)
