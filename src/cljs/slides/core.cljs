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

(defn next-slide-path [model f]
  (let [slides (:slides @model)
        current-pos (:index @model)
        upcoming-pos (f (js/parseInt current-pos))
        index (if (within-slides? upcoming-pos model) upcoming-pos current-pos)]
    (apply str (interpose "/" ["#" "slides" index]))))

(defn set-editing-state [owner editing text]
  (om/set-state! owner :editing editing)
  (om/set-state! owner :text text)
  (js/setTimeout #(.focus (om/get-node owner "title-edit")) 100))

(defn update-title-on-enter [event owner model]
  (let [key-code (.. event -keyCode)
        new-title (om/get-state owner :text)]
    (when (= key-code 13)
      (om/update! model :title new-title)
      (set-editing-state owner false new-title))))

(defn slide-title [model owner]
  (reify
    om/IInitState
    (init-state [_]
      {:editing false
       :text ""})
    om/IRenderState
    (render-state [_ state]
      (dom/div #js {:className (when (om/get-state owner :editing) "editing")}
               (dom/input #js {:className "title-edit"
                               :value (om/get-state owner :text)
                               :ref "title-edit"
                               :onChange #(om/set-state! owner :text (.. % -target -value))
                               :onBlur #(om/set-state! owner :editing false)
                               :onKeyUp #(update-title-on-enter % owner model)})
               (dom/h1 #js {:className "title-display"
                            :onClick #(set-editing-state owner true (:title model))} (:title model)
                       (dom/small #js {:className "edit"} " ✎"))))))

(defn slide [model owner]
  (reify
    om/IRender
    (render [_]
      (dom/section #js {:className "slide"}
                   (when-let [bg (:bg model)]
                     (dom/img #js {:className "bg" :src bg}))
                   (dom/div #js {:className "slide-content banner"}
                            )))))

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

(defn main []
  (om/root app app-state {:target (. js/document (getElementById "app"))}))

(def slide-imgs
  [{:title "A Bird's Eye View of ClojureScript"
    :bg "/images/gull.jpg"}
   {:title "Using Om Components"
    :bg "/images/gull2.jpg"}
   {:title "Event Handling"
    :bg "/images/swan.jpg"}
   {:title "Client-side Routing"
    :bg "/images/heron.jpg"}
   {:title "Adding Local State"
    :bg "/images/raven.jpg"}])

(defn main []
  (om/root app app-state {:target (. js/document (getElementById "app"))}))

(swap! app-state assoc :slides slide-imgs)
(sec/set-config! :prefix "#")
(handle-navigation)
