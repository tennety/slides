(ns slides.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [secretary.core :as sec :include-macros true :refer-macros [defroute]]
            [goog.events :as events]
            [goog.history.EventType :as EventType])
  (:import goog.History))

(defonce app-state (atom {:slides [] :index 0}))

(defn within-slides? [index model]
   (let [length (count (:slides @model))]
     (and (>= index 0) (< index length))))

(defroute "/" []
  (.setToken (History.) "/slides/0"))

(defroute slide-path "/slides/:index" [index]
  (let [index (js/parseInt index)]
    (when (within-slides? index app-state)
    (swap! app-state assoc :index index))))

(defn handle-navigation []
  (let [history (History.)
        navigation EventType/NAVIGATE]
    (goog.events/listen history
                        navigation
                        #(-> % .-token sec/dispatch!))
    (.setEnabled history true)))

(defn slide [model owner]
  (reify
    om/IRender
    (render [_]
      (dom/section #js {:className "slide"}
                   (when-let [bg (:bg model)]
                     (dom/img #js {:className "bg" :src bg}))
                   (dom/div #js {:className "slide-content banner"}
                            (dom/h1 #js {} (:title model)))))))

(defn next-slide-path [model f]
  (let [slides (:slides @model)
        current-pos (:index @model)
        upcoming-pos (f (js/parseInt current-pos))
        index (if (within-slides? upcoming-pos model) upcoming-pos current-pos)]
    (slide-path {:index index})))

(defn app [model owner]
  (reify
    om/IRender
    (render [_]
      (dom/div #js {:className "content"}
               (dom/a #js {:className "control banner previous"
                           :href (next-slide-path model dec)})
               (om/build slide ((:slides model) (:index model)))
               (dom/a #js {:className "control banner next"
                           :href (next-slide-path model inc)})))))

(def slide-imgs
  [{:title "A Bird's Eye View of ClojureScript"
    :bg "/images/gull.jpg"}
   {:title "Using Om Components"
    :bg "/images/gull2.jpg"}
   {:title "Event Handling"
    :bg "/images/swan.jpg"}
   {:title "Client-side Routing"
    :bg "/images/heron.jpg"}])

(defn main []
  (om/root app app-state {:target (. js/document (getElementById "app"))}))

(swap! app-state assoc :slides slide-imgs)
(sec/set-config! :prefix "#")
(handle-navigation)
