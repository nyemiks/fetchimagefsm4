(ns fetchimagefsm4.core
  (:require
   [reagent.dom :as rdom]
   [re-frame.core :as re-frame]
   [fetchimagefsm4.events :as events]
   [fetchimagefsm4.views :as views]
   [fetchimagefsm4.config :as config]
   [re-frisk.core :refer [enable-re-frisk!]]
   ))


(defn dev-setup []
  (when config/debug?
    (enable-re-frisk!)
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
   ; (rdom/render [views/main-panel] root-el)
     (rdom/render [views/image-viewer] root-el)
    )
  )

(defn init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
