(defproject hello-lambda "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.2-rc1"]
                 [fierycod/holy-lambda "0.0.8"]]
  :global-vars {*warn-on-reflection* true}
  :main hello-lambda.core
  :profiles {:uberjar {:aot [hello-lambda.core]
                       :jvm-opts ["-Dclojure.spec.skip-macros=true"]}}
  :uberjar-name "output.jar")
