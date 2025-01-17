(ns datalevin.test.lru
  (:require
   #?(:cljs [cljs.test    :as t :refer-macros [is are deftest testing]]
      :clj  [clojure.test :as t :refer        [is are deftest testing]])
   [datalevin.lru :as lru])
  (:import [datalevin.lru LRU]))

(deftest test-lru
  (let [^LRU l0 (lru/lru 2 (System/currentTimeMillis))
        ^LRU l1 (assoc l0 :a 1)
        ^LRU l2 (assoc l1 :b 2)
        ^LRU l3 (assoc l2 :c 3)
        ^LRU l4 (assoc l3 :b 4)
        ^LRU l5 (assoc l4 :d 5)]
    (is (= (.-target l0) (.-target l1) (.-target l2)
           (.-target l3) (.-target l4) (.-target l5)))
    (are [l k v] (= (get l k) v)
      l0 :a nil
      l1 :a 1
      l2 :a 1
      l2 :b 2
      l3 :a nil ;; :a get evicted on third insert
      l3 :b 2
      l3 :c 3
      l4 :b 2   ;; assoc updates access time, but does not change a value
      l4 :c 3
      l5 :b 2   ;; :b remains
      l5 :c nil ;; :c gets evicted as the oldest one
      l5 :d 5)))
