(defproject jikken "0.1.0-SNAPSHOT"
            :plugins [[lein-cljsbuild "0.2.9"]
                      [lein-swank "1.4.4"]]
            :description "FIXME: write this!"
            :dependencies [[org.clojure/clojure "1.4.0"]
                           [domina "1.0.1"]
                           [fetch "0.1.0-alpha2"]
                           [crate "0.2.1"]
                           [noir "1.3.0-beta3"]
                           [com.datomic/datomic-free "0.8.3599"]]
            :min-lein-version "2.0.0"
            :repl-options {:nrepl-middleware
                           [cemerick.piggieback/wrap-cljs-repl]}
            :profiles {:production
                       {:hooks [leiningen.cljsbuild]}}
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
                           :optimizations :advanced}}}
                        }
            :main jikken.server)
