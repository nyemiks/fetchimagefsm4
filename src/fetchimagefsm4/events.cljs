(ns fetchimagefsm4.events
  (:require
   [re-frame.core :as re-frame]
   [fetchimagefsm4.db :as db]
   ))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))




(re-frame/reg-event-db
 ::update-image
 (fn [db [_ url]]
              (.info js/console "image updated. url " url)        
          
            (assoc db :image url)
            )
 )
