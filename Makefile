SHELL := /usr/bin/env bash

# -e:          abort on error
# -u:          error on unset variable
# -o pipefail: pipeline fails if any element fails
# -c:          run command
.SHELLFLAGS := -e -u -o pipefail -c

# add all phony targets here
.PHONY: help init

## help: Show this help message
help:
	@echo -e "\n  Commands for managing geheim"
	@echo -e "\n  \033[1mUsage:    make\033[0m \033[36m<command>\033[0m\n"
	@awk 'BEGIN{FS=":"}; \
		  /^# ---/ { \
		  	if (match($$0, /^# --- (.+?) -/, m)) { \
		   		printf "\n  %s\n\n", toupper(m[1]) \
		  	}\
		  }; \
		  /^## [a-zA-Z_-]+:/ { \
		  	sub(/^## /, "  "); \
		  	split($$0, parts, ": "); \
		  	printf "  \033[36m%-18s\033[0m %s\n", parts[1], parts[2] \
		  } ' $(MAKEFILE_LIST)
	@echo -e "\n"


# --- normal use --- #

## up-db: Start the database container
up-db:
	docker compose -f compose.yml up -d db

## dev: Run the dev server
dev: up-db
	SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run

## test: Run unit and slice tests
test:
	SPRING_PROFILES_ACTIVE=test ./mvnw test

## verify: Run all tests and check code quality
verify:
	SPRING_PROFILES_ACTIVE=test ./mvnw verify
