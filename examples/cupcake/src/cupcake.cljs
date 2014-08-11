;;
;; the cupcake example using some extras:
;; - the abbreviated version of pixi components found in 'omreactpixi.abbrev'
;; - defcomponentk version of defcomponent from om-tools, which uses fnk syntax
;;

(ns omreactpixi.examples.cupcake
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [goog.events :as events]
            [om.core :as om :include-macros true]
            [schema.core :as schema]
            [om-tools.core :as omtools :include-macros true]
            [omreactpixi.abbrev :as pixi]
            [clojure.string :as string]
            [cljs.core.async :as async :refer [>! <! put!]]))


(defn assetpath [name] (str "../assets/" name))

(def flavormapping
  {
   :vanilla (assetpath "creamVanilla.png")
   :chocolate (assetpath "creamChoco.png")
   :mocha (assetpath "creamMocha.png")
   :pink (assetpath "createmPink.png")
   })

(omtools/defcomponentk cupcakecomponent [[:data topping xposition] owner]
  (render [_]
          (let [creamimage (flavormapping topping)
                cakeimage (assetpath "cupCake.png")
                spritecenter (PIXI.Point. 0.5 0.5)]
            (pixi/displayobjectcontainer {:x xposition, :y 100}
                                         (pixi/sprite {:image creamimage :y -35 :anchor spritecenter :key "topping"})
                                         (pixi/sprite {:image cakeimage :y 35 :anchor spritecenter :key "cake"}))))
  (display-name [_] "Cupcake"))

(omtools/defcomponentk cupcakestage [[:data width height xposition topping] owner]
  (render [_]
          (pixi/stage {:width width :height height}
                      (pixi/tilingsprite {:image (assetpath "bg_castle.png") :width width :height height :key 1})
                      (om/build cupcakecomponent {:topping topping :xposition xposition :ref "cupcake" :key 2})
                      (pixi/text {:text "Vector text" :x xposition :y 10 :style #js {:font "40px Times"} :anchor (PIXI.Point. 0.5 0) :key 3})
                      (pixi/bitmaptext {:text "Bitmap text" :x xposition :y 180 :tint 16rff88ff88 :style {:font "40 Comic_Neue_Angular"} :key 4})))
  (display-name [_] "CupcakeStage"))


(defn startcupcakes [w h]
  (om/root cupcakestage  {:width w :height h :xposition 100 :topping :vanilla}
           {:target (.getElementById js/document "my-app")}))

;; gotta load the bitmap font first or else pixi bombs out

(let [inset #(- % 16)
      w (-> js/window .-innerWidth inset)
      h (-> js/window .-innerHeight inset)
      fontloader (PIXI.BitmapFontLoader. (assetpath "comic_neue_angular_bold.fnt"))]
  (.on fontloader "loaded" #(startcupcakes w h))
  (.load fontloader))
