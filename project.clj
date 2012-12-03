(defproject jida "0.1.0-SNAPSHOT"
            :plugins [[lein-cljsbuild "0.2.9"]
                      [yayitswei/lein-deploy-app "0.1.0-SNAPSHOT"]]
            :description "hosted codeq"
            :dependencies [[org.clojure/clojure "1.4.0"]
                           [com.taoensso/carmine "1.0.0"]
                           [org.clojure/google-closure-library "0.0-2029"]
                           [org.clojure/google-closure-library-third-party "0.0-2029"]
                           [domina "1.0.1"]
                           [fetch "0.1.0-alpha2"]
                           [crate "0.2.1"]
                           [noir "1.3.0-beta3"]
                           [com.datomic/datomic-free "0.8.3599"]
                           [org.clojure/data.json "0.2.1"]
                           [com.novemberain/monger "1.4.0"]
                           [clj-redis "0.0.12"]]
            :min-lein-version "2.0.0"
            :cljsbuild {:builds
                        {:dev
                         {:source-path "src/cljs"
                          :compiler
                          {:output-to "resources/public/js/bin-debug/main.js"
                           :output-dir "resources/public/js/bin-debug"
                           :optimizations :whitespace
                           :pretty-print true}}
                         :prod
                         {:source-path "src/cljs"
                          :compiler
                          {:output-to "resources/public/js/bin/main.js"
                           :output-dir "resources/public/js/bin"
                           :optimizations :simple}}}
                        }
            :s3 {:bucket "jida"
                 :root "resources/public/"
                 :files ["js/bin/main.js"]}
            :main jida.server)
