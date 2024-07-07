#!/bin/bash

echo "--------------- 서버 배포 시작 -----------------"
docker stop test-deploy-server || true
docker rm test-deploy-server || true
docker pull /test-deploy-ecr:latest
docker run -d --name test-deploy-server -p 8080:8080 ${{ steps.login-ecr.outputs.registry }}/test-deploy-ecr:latest
echo "--------------- 서버 배포 끝 -----------------"
