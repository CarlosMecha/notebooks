
clean: 
	mvn clean

build-db:
	docker build --rm -t carlosmecha/notebooks-dev-db ./database

build: 
	mvn package
	docker build -t carlosmecha/notebooks-dev .

run:
	mvn spring-boot:run

run-db:
	docker run --rm -p 5432:5432 carlosmecha/notebooks-db-dev:latest

.PHONY: build, build-db, clean, run, run-db