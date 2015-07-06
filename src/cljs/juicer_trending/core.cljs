(ns juicer-trending.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs-http.client :as http]
            [cljs.core.async :refer  [<!]]))

(enable-console-print!)

(def url "http://data.test.bbc.co.uk/bbcrd-juicer/articles?trending=true&published_after=2015-07-06&apikey=JUICER_API_KEY")

(def dbpedia-thing "http://dbpedia.org/resource/Thing")
(def dbpedia-person "http://dbpedia.org/ontology/Person")

(defonce app-state
  (atom {:people [{:id "loading..."}] :things [{:id "loading..."}]}))

(defn get-category-items [dbpedia-cat juicer-tags]
  (->> juicer-tags
       :body
       :trending
       :items
       (into {} (filter (fn [i] (= (:id i) dbpedia-cat))))
       :items))

(go (let [response (<! (http/get url {:with-credentials? false}))
          cursor (om/root-cursor app-state)
          things (get-category-items dbpedia-thing response)
          people (get-category-items dbpedia-person response)]
      (om/update! cursor {:things things :people people})))

(defn tag [item owner]
  (reify
    om/IRender
    (render [this]
      (dom/p nil (:id item)))))

(defn trends [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/div nil
               (dom/h2 nil "Trending People")
               (apply dom/div nil
                      (om/build-all tag (:people data)))
               (dom/h2 nil "Trending Things")
               (apply dom/div nil
                      (om/build-all tag (:things data)))))))

(defn main []
  (om/root trends app-state
           {:target (. js/document (getElementById "app"))}))
