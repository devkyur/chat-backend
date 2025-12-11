#!/bin/bash

# SSL 인증서 생성 스크립트

echo "Creating self-signed SSL certificate for localhost..."

# 인증서 저장 디렉토리 생성
mkdir -p src/main/resources/ssl

# 자체 서명 인증서 생성 (2048-bit RSA, 365일 유효)
keytool -genkeypair \
  -alias dating-app \
  -keyalg RSA \
  -keysize 2048 \
  -storetype PKCS12 \
  -keystore src/main/resources/ssl/keystore.p12 \
  -validity 365 \
  -storepass changeit \
  -keypass changeit \
  -dname "CN=localhost, OU=Development, O=DatingApp, L=Seoul, ST=Seoul, C=KR"

echo "Certificate created successfully at src/main/resources/ssl/keystore.p12"
echo "Password: changeit"
