SHELL := /bin/bash
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

.PHONY: all clean deploy destroy destroy-bucket dry-api install-holy-lambda logs-tail make-bucket native-compile native-deploy native-dry-api native-invoke native-pack
BUCKET_NAME=holy-lambda--hello-lambda
STACK_NAME=holy-lambda--hello-lambda
APP_REGION=eu-central-1
LAMBDA_NAME=HelloLambdaFunction
PWD=$$(pwd)

UNAME_S := $(shell uname -s)
ifeq ($(UNAME_S),Linux)
	native_image_cmd=native-image -jar target/output.jar --report-unsupported-elements-at-runtime -H:ConfigurationFileDirectories=resources/native-configuration --no-fallback --enable-url-protocols=http,https --no-server  --initialize-at-build-time
	DRY_API_WARNING=""
else
	native_image_cmd=docker run -v ${PWD}:/project:Z -it fierycod/graalvm-native-image:latest bash -c "cd /project && native-image -jar target/output.jar --report-unsupported-elements-at-runtime -H:ConfigurationFileDirectories=resources/native-configuration --no-fallback --enable-url-protocols=http,https --no-server  --initialize-at-build-time "
	DRY_API_WARNING="'dry-api' does not work on your platform: use 'native-dry-api' instead to run within Docker."
endif

all: native-deploy

clean: destroy
	-rm -rf target/ packaged.yml resources/native-packaged.yml output

deploy: pack
	@sam deploy --template-file ./packaged.yml --stack-name $(STACK_NAME) --capabilities CAPABILITY_IAM --region $(APP_REGION)

destroy:
	@aws cloudformation delete-stack --stack-name $(STACK_NAME) --region $(APP_REGION)

destroy-bucket:
	@aws s3 rb s3://$(BUCKET_NAME) --force --region $(APP_REGION)

dry-api: target/output.jar
	@if [ ${DRY_API_WARNING} == '' ]; then sam local start-api --skip-pull-image; else echo ${DRY_API_WARNING}; fi

install-holy-lambda:
	@bash -c "cd ../../ && lein install"

logs-tail:
	sam logs -n $(LAMBDA_NAME) --stack-name $(STACK_NAME) -t

make-bucket:
	@aws s3 ls s3://$(BUCKET_NAME) || aws s3 mb s3://$(BUCKET_NAME)

native-compile: output
	@mv -f output server
	@zip -j latest resources/bootstrap server
	@mv latest.zip resources/
	@rm -Rf server

native-deploy: native-pack
	@sam deploy --template-file ./resources/native-packaged.yml --stack-name $(STACK_NAME) --capabilities CAPABILITY_IAM --region $(APP_REGION)

native-dry-api: native-compile
	@sam local start-api --template ./resources/native-template.yml --skip-pull-image

native-invoke: 
	sam local invoke $(LAMBDA_NAME) --template ./resources/native-template.yml --skip-pull-image -e $(EVENT_PAYLOAD_FILE)

native-pack: native-compile make-bucket
	@sam package --template-file ./resources/native-template.yml --output-template-file resources/native-packaged.yml --s3-bucket $(BUCKET_NAME) --s3-prefix "hello-lambda-latest"

output: target/output.jar
	@${native_image_cmd}

pack: target/output.jar make-bucket
	@sam package --template-file ./template.yml --output-template-file packaged.yml --s3-bucket $(BUCKET_NAME) --s3-prefix "hello-lambda-latest"

target/output.jar: src/hello_lambda/core.clj
	@lein uberjar


