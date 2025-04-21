#!/bin/bash

echo "======================================================"
echo " Iniciando containers Docker para Sales API"
echo "======================================================"

# Verificar se o Docker está instalado
if ! command -v docker &> /dev/null; then
    echo "Erro: Docker não está instalado ou não está no PATH."
    echo "Por favor, instale o Docker para seu sistema operacional."
    echo "https://www.docker.com/get-started"
    exit 1
fi

# Verificar se o Docker Compose está instalado
if ! command -v docker-compose &> /dev/null; then
    echo "Erro: Docker Compose não está instalado ou não está no PATH."
    echo "Por favor, instale o Docker Compose."
    echo "https://docs.docker.com/compose/install/"
    exit 1
fi

# Construir e iniciar os containers
echo ""
echo "Construindo e iniciando os containers..."
echo ""
docker-compose up -d

if [ $? -ne 0 ]; then
    echo ""
    echo "Erro ao iniciar os containers. Verifique os logs acima."
    exit 1
fi

echo ""
echo "======================================================"
echo " Containers iniciados com sucesso!"
echo "======================================================"
echo ""
echo "Acesse a aplicação em: http://localhost:8080"
echo "Documentação Swagger: http://localhost:8080/swagger-ui.html"
echo "PgAdmin: http://localhost:5050"
echo "  - Email: admin@marketplace.com"
echo "  - Senha: admin"
echo ""
echo "Para visualizar os logs: docker-compose logs -f app"
echo "Para parar os containers: docker-compose down"
echo ""