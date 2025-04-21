@echo off
echo ======================================================
echo  Iniciando containers Docker para Sales API
echo ======================================================

REM Verificar se o Docker está instalado
docker --version > nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo Erro: Docker nao esta instalado ou nao esta no PATH.
    echo Por favor, instale o Docker Desktop para Windows.
    echo https://www.docker.com/products/docker-desktop
    exit /b 1
)

REM Verificar se o Docker Desktop está em execução
docker info > nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo Erro: O Docker Desktop nao esta em execucao.
    echo Por favor, inicie o Docker Desktop e tente novamente.
    echo.
    echo Passos para iniciar o Docker Desktop:
    echo 1. Procure por "Docker Desktop" no menu Iniciar
    echo 2. Clique para iniciar o aplicativo
    echo 3. Aguarde ate que o icone do Docker na barra de tarefas fique estavel
    echo 4. Execute este script novamente
    exit /b 1
)

REM Verificar se o arquivo docker-compose.yml existe
if not exist docker-compose.yml (
    echo Erro: Arquivo docker-compose.yml nao encontrado.
    echo Verifique se voce esta executando este script no diretorio correto.
    exit /b 1
)

REM Construir e iniciar os containers
echo.
echo Construindo e iniciando os containers...
echo.

REM Tentar iniciar os containers com retry
SETLOCAL ENABLEDELAYEDEXPANSION
set RETRY_COUNT=0
set MAX_RETRIES=2

:RETRY
docker-compose up -d

if %ERRORLEVEL% NEQ 0 (
    set /a RETRY_COUNT+=1
    if !RETRY_COUNT! LEQ %MAX_RETRIES% (
        echo.
        echo Tentativa !RETRY_COUNT! de %MAX_RETRIES% falhou.
        echo Aguardando 5 segundos antes de tentar novamente...
        timeout /t 5 /nobreak > nul
        echo Tentando novamente...
        goto RETRY
    ) else (
        echo.
        echo Erro ao iniciar os containers apos %MAX_RETRIES% tentativas.
        echo Verifique se:
        echo 1. O Docker Desktop esta completamente inicializado
        echo 2. As portas necessarias (8080, 5432, 5050) nao estao em uso
        echo 3. Ha espaco em disco suficiente
        echo.
        echo Para mais detalhes, execute: docker-compose logs
        exit /b 1
    )
)
ENDLOCAL

echo.
echo ======================================================
echo  Containers iniciados com sucesso!
echo ======================================================
echo.
echo Acesse a aplicacao em: http://localhost:8080
echo Documentacao Swagger: http://localhost:8080/swagger-ui.html
echo PgAdmin: http://localhost:5050
echo   - Email: admin@marketplace.com
echo   - Senha: admin
echo.
echo Para visualizar os logs: docker-compose logs -f app
echo Para parar os containers: docker-compose down
echo.

pause