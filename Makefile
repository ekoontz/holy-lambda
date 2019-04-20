SHELL := /bin/bash
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

.PHONY: use-nvm setup release

.DEFAULT_GOAL := setup
VERSION=$(cat project.clj | egrep -o -m 1 '[0-9].[0-9].[0-9].*')

use-nvm:
	@. ~/.nvm/nvm.sh && nvm use > /dev/null 2>&1

setup: use-nvm
	@printf "\n ${GREEN} Installing Clojure dependencies ${NC}\n"
	@lein deps
	@printf "\n ${GREEN} Successfully installed all Clojure dependencies ${NC}\n"
	@printf "\n ${GREEN} Installing all node modules ${NC}\n"
	@npm install -g yarn
	@yarn
	@printf "${GREEN} Successfully installed all Node.js dependencies ${NC}\n"
	@printf "\n ${GREEN} Installing fnative... ${NC}\n"
	@bash -c "chmod +x resources/fnative && sudo cp resources/fnative /usr/local/bin/"
	@printf "\n ${GREEN} Sucessfully installed fnative utility... ${NC}\n"

release: use-nvm
	@sh ./resources/github-tag.sh $(VERSION)

commit: use-nvm
	@yarn commit