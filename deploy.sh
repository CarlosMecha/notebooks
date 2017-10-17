#!/bin/bash

VERSION=$(mvn -q -Dexec.executable='echo' -Dexec.args='${project.version}' org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)

echo "Building notebooks:$VERSION"
docker build -t carlosmecha/notebooks .
docker tag carlosmecha/notebooks:latest carlosmecha/notebooks:$VERSION
docker tag carlosmecha/notebooks:latest 737277841206.dkr.ecr.us-west-2.amazonaws.com/notebooks:latest
echo "Pushing notebooks:$VERSION to ECR"
docker push 737277841206.dkr.ecr.us-west-2.amazonaws.com/notebooks:latest