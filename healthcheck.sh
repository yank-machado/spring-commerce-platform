#!/bin/bash

# Script para verificar a saúde da aplicação

APP_URL="http://localhost:8080/actuator/health"

echo "Verificando a saúde da aplicação em $APP_URL..."

response=$(curl -s -o /dev/null -w "%{http_code}" $APP_URL)

if [ "$response" = "200" ]; then
    echo "A aplicação está saudável!"
    exit 0
else
    echo "A aplicação não está respondendo corretamente. Código HTTP: $response"
    exit 1
fi