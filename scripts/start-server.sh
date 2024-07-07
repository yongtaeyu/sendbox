#!/bin/bash

echo "--------------- 서버 배포 시작 -----------------"
docker stop test-deploy-server || true
docker rm test-deploy-server || true
docker pull 860822614692.dkr.ecr.ap-northeast-2.amazonaws.com/test-deploy-ecr
docker run -d --name test-deploy-server -p 8080:8080 860822614692.dkr.ecr.ap-northeast-2.amazonaws.com/test-deploy-ecr:latest
echo "--------------- 서버 배포 끝 -----------------"
