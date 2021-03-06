(ns fierycod.holy-lambda.impl.agent-test
  (:require
   [fierycod.holy-lambda.core :as h]
   [fierycod.holy-lambda.impl.agent :as a]
   [clojure.test :as t]))

(h/deflambda HolyLambda
  [event context]
  (println event context))

(h/deflambda NewLambda
  [event context]
  (println event context))

(t/deftest native-agents-files->payloads-map-fn-test
  (t/testing "should generate the map from pairs [HandlerName, [{:event sth, :context sth, :envs sth, :path sth}, ...]]"
    (t/is (= '({:context {},
                :event {},
                :name "fierycod.holy-lambda.impl.agent-test.HolyLambda",
                :path "resources/native-agents-payloads/1.json",
                :propagate false}
               {:context {},
                :event {:lambda2 2},
                :name "fierycod.holy-lambda.impl.agent-test.HolyLambda",
                :path "resources/native-agents-payloads/2.json",
                :propagate false}
               {:context {:ctx "CTX"},
                :event {:msg "new-lambda"},
                :name "fierycod.holy-lambda.impl.agent-test.NewLambda",
                :path "resources/native-agents-payloads/3.json",
                :propagate false})
             (#'fierycod.holy-lambda.impl.agent/native-agents-files->payloads-map)))))

(t/deftest call-lambdas-with-agent-payloads-fn-test
  (t/testing "should call all lambdas with corresponding payloads and report on each step"
    (t/is (= "[INFO] Calling lambda fierycod.holy-lambda.impl.agent-test.HolyLambda with payloads from resources/native-agents-payloads/1.json\n{} {}\n[INFO] Succesfully called fierycod.holy-lambda.impl.agent-test.HolyLambda with payloads from resources/native-agents-payloads/1.json\n[INFO] Calling lambda fierycod.holy-lambda.impl.agent-test.HolyLambda with payloads from resources/native-agents-payloads/2.json\n{:lambda2 2} {}\n[INFO] Succesfully called fierycod.holy-lambda.impl.agent-test.HolyLambda with payloads from resources/native-agents-payloads/2.json\n[INFO] Calling lambda fierycod.holy-lambda.impl.agent-test.NewLambda with payloads from resources/native-agents-payloads/3.json\n{:msg new-lambda} {:ctx CTX}\n[INFO] Succesfully called fierycod.holy-lambda.impl.agent-test.NewLambda with payloads from resources/native-agents-payloads/3.json\n[INFO] Succesfully called all the lambdas\n"
             (with-out-str (a/call-lambdas-with-agent-payloads {"fierycod.holy-lambda.impl.agent-test.HolyLambda" #'HolyLambda,
                                                                "fierycod.holy-lambda.impl.agent-test.NewLambda" #'NewLambda}))))))
