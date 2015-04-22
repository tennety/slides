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
                   (dom/div #js {:className "slide-content banner"}
                            (dom/h1 #js {} (:title model)))))))

(defn next-slide [e model f]
  (let [slides (:slides @model)
        current-pos (:index @model)
        upcoming-pos (f current-pos)]
    (.preventDefault e)
    (when (get slides upcoming-pos)
      (om/transact! model :index f))))

(defn app [model owner]
  (reify
    om/IRender
    (render [_]
      (dom/div #js {:className "content"}
               (dom/a #js {:className "control banner previous"
                           :href ""
                           :onClick #(next-slide % model dec)})
               (om/build slide ((:slides model) (:index model)))
               (dom/a #js {:className "control banner next"
                           :href ""
                           :onClick #(next-slide % model inc)})))))

(def slide-imgs
  [{:title "A Bird's Eye View of ClojureScript"
    :bg "/images/gull.jpg"}
   {:title "Using Om Components"
    :bg "/images/gull2.jpg"}
   {:title "Event Handling"
    :bg "/images/swan.jpg"}])

(defn main []
  (om/root app app-state {:target (. js/document (getElementById "app"))}))

(swap! app-state assoc :slides slide-imgs)

;;(swap! app-state assoc :index 1)
