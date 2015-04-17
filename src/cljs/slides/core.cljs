(ns slides.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(defonce app-state (atom {:slides [] :index 0}))

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
               (om/build slide ((:slides model) (:index model)))))))

(def slide-imgs
  [{:title "A Bird's Eye View of ClojureScript"
    :bg "/images/gull.jpg"}
   {:title "Using Om Components"
    :bg "/images/gull2.jpg"}])

(defn main []
  (om/root app app-state {:target (. js/document (getElementById "app"))}))

(swap! app-state assoc :slides slide-imgs)

;;(swap! app-state assoc :index 0)
