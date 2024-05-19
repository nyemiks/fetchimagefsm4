(ns fetchimagefsm4.views
  (:require
   [re-frame.core :as re-frame]
   [fetchimagefsm4.subs :as subs]
    [fetchimagefsm4.events :as events]
   [ajax.protocols :refer [-body]]
    [re-statecharts.core :as rs]
    [statecharts.core :as fsm :refer [assign]] 
   [statecharts.integrations.re-frame :as fsm.rf]
   ))



(def image-url "https://picsum.photos/200/300")




(defn load-image [state data]
   (.info js/console " -- load image -- ")
  (.info js/console " context:  " state)
   (.info js/console " data:  " data)
  

  (.info js/console " call-fx ... ")

 ;  (re-frame/dispatch [::events/get-image image-url ])
  
    (fsm.rf/call-fx
   {:http-xhrio
    {
     :uri image-url
     :method :get
     :response-format {
                        :content-type "image/png" 
                        :type :blob 
                        :description "PNG file" 
                        :read -body
                       } 
     
      :on-failure [::fail-load :fetch-image-open ]
      :on-success [::success-load :fetch-image-open ]
      }
    }
   )

  )



(defn on-image-loaded [state {:keys [data]}]
   (.info js/console "-- on image loaded -- " )
   (.info js/console "context: " state)
 ; (assoc state :friends (:friends data))
    (.info js/console "data: " data)
  
  (let [
        url (.createObjectURL js/URL data)
     ;   state @(re-frame/subscribe [::rs/state :imageFetcher])
        ; _  (.info js/console "initial state" state)
  ]
      ; (.info js/console "image json response: " url)        
                
    (re-frame/dispatch [::events/update-image url])  ;;  
   ; (.info js/console "current fsm state:" state)
    )
  
    
  )



(defn on-image-load-failed [state {:keys [data]}]
  (.info js/console " -- on image load failed -- " )
   (.info js/console "context: " state)
    (assoc state :error (:status data))
  )



(def fetch-image-fsm 
   {
     :id :imageFetcher,
     :initial ::ready,
     :context {
       :image nil
     },
   :entry   (fn [& _]
                           (.info js/console "fm initialized ! just before ready state ")
                            )
    :states {
       ::ready {
                   :entry  (fn [& _]
                             (.info js/console "now in ready state ! ")
                             )
                   :on {
                          ::fetch-image ::fetching
                        }
                },
       ::fetching {
                  :entry  load-image
                                                                             
                  :on  {
                          ::success-load {
                                          :actions (assign on-image-loaded)
                                          :target  ::success
                                       }
                           ::fail-load    {
                                            :actions on-image-load-failed
                                            :target  ::error
                                          }
                        }
			      }, 
              ::success {
                         :entry  (fn [state evt]
                             (.info js/console "now in success state ! ")
                                   (.info js/console "state: " state)
                                   (.info js/console "evt: " evt)
                             )

              },
              ::error {}
             }    
       
     }
  
  )


(def global-image-fsm 
  (->
      fetch-image-fsm
      (with-meta {::rs/open? true})
      (assoc :id :fetch-image-open)
   )
  )




(defn image-viewer []
  (let
       [
         name (re-frame/subscribe [::subs/name])
          _  (.info js/console "start global image fsm ...") 
          _  (re-frame/dispatch [::rs/start global-image-fsm])
          _  (.info js/console "global image fsm started !")
          
         state (re-frame/subscribe [::rs/state :fetch-image-open])
       
       ;  image (re-frame/subscribe [::subs/image])        
        ]
     (.info js/console "current state: " @state)
      (fn []
       [:section
        [:h1
          "Hello from " @name]
     
        (when (= @state ::fetching) [:p "loading..."])
        (when (= @state ::success)  [:p "success !!"]
              )
        (when (= @state ::error) [:p "An error occured"])
        [:button {
                  :on-click (fn []
                              (.info js/console "get image ... ")
                               ;  (re-frame/dispatch [::rs/transition :imageFetcher ::BUTTON_CLICKED])                                                         
                              (re-frame/dispatch [::fetch-image :fetch-image-open ])          
                              )
                  } "Get Image"]
       ])
      ; (finally (re-frame/dispatch [::rs/restart (:id fetch-image-fsm)]))
       )
  )





(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1
      "Hello from " @name]
     ]))
