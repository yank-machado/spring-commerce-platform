# 🐳 Configuração Docker para Sales API

## 📋 Visão Geral

Este projeto está configurado para ser executado em containers Docker, facilitando a implantação e garantindo consistência entre ambientes de desenvolvimento, teste e produção. A configuração Docker permite executar a aplicação, banco de dados e ferramentas administrativas em um ambiente isolado e reproduzível.

## 🚀 Estrutura da Configuração Docker

A configuração Docker deste projeto inclui:

1. **Dockerfile**: Define a imagem da aplicação Spring Boot com base em Eclipse Temurin JDK/JRE
2. **docker-compose.yml**: Orquestra os serviços necessários (aplicação, banco de dados e pgAdmin)
3. **.env**: Centraliza as variáveis de ambiente para todos os serviços
4. **init-db.sql**: Script de inicialização do banco de dados PostgreSQL com extensões e otimizações
5. **Scripts de execução**: `docker-run.bat` (Windows) e `docker-run.sh` (Linux/Mac) para facilitar a inicialização
6. **.dockerignore**: Define quais arquivos e diretórios devem ser ignorados durante o build da imagem

## 📋 Pré-requisitos

- [Docker](https://www.docker.com/get-started) instalado
- [Docker Compose](https://docs.docker.com/compose/install/) instalado

## 🛠️ Serviços Configurados

### Aplicação Spring Boot (sales-api)
- Construída a partir do código-fonte usando Maven em um processo multi-estágio
- Configurada para conectar automaticamente ao PostgreSQL com parâmetros de conexão configuráveis
- Exposta na porta 8080 (configurável no arquivo .env)
- Utiliza o perfil "prod" com configurações otimizadas para ambiente de produção
- Configurada com healthcheck para monitoramento de saúde da aplicação
- Dependências gerenciadas pelo Spring Boot para garantir compatibilidade
- Configurada para aguardar o banco de dados estar pronto antes de iniciar

### Banco de Dados PostgreSQL (sales-db)
- Imagem PostgreSQL 15 Alpine (leve e segura)
- Dados persistidos em volume Docker para manter os dados entre reinicializações
- Inicializado com extensões úteis (uuid-ossp, pg_trgm) e configurações de performance
- Exposto na porta 5432 (configurável no arquivo .env)
- Configurado com healthcheck para garantir que a aplicação só inicie após o banco estar pronto
- Otimizações de performance configuradas no script init-db.sql

### PgAdmin (Interface Web para PostgreSQL)
- Interface gráfica para gerenciamento do banco de dados
- Exposta na porta 5050 (configurável no arquivo .env)
- Credenciais configuráveis no arquivo .env
- Dados persistidos em volume Docker para manter configurações e conexões
- Configurado para iniciar apenas após o banco de dados estar saudável

## 🚀 Como Executar

### No Windows:

```bash
# Execute o script batch
docker-run.bat
```

O script Windows verifica se o Docker está instalado, inicia os containers e exibe informações de acesso aos serviços.

### No Linux/Mac:

```bash
# Dê permissão de execução ao script
chmod +x docker-run.sh

# Execute o script
./docker-run.sh
```

O script Linux/Mac verifica se o Docker e o Docker Compose estão instalados, inicia os containers e exibe informações de acesso aos serviços.

### Manualmente:

```bash
# Construir e iniciar todos os serviços em segundo plano
docker-compose up -d

# Verificar os logs da aplicação
docker-compose logs -f app

# Parar todos os serviços
docker-compose down
```

## 🌐 Acessando os Serviços

- **Aplicação**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **PgAdmin**: http://localhost:5050
  - Email: admin@marketplace.com
  - Senha: admin

### Configuração do PgAdmin

1. Acesse o PgAdmin em http://localhost:5050
2. Faça login com as credenciais acima
3. Adicione um novo servidor:
   - Nome: sales-db
   - Host: db
   - Porta: 5432
   - Banco de dados: marketplacedb
   - Usuário: postgres
   - Senha: postgres

## ⚙️ Personalizando a Configuração

Edite o arquivo `.env` para personalizar:

- Portas dos serviços
- Credenciais do banco de dados
- Credenciais do PgAdmin
- Perfil ativo do Spring

### Exemplo de arquivo .env

```
# Configurações da Aplicação
APP_PORT=8080
SPRING_PROFILES_ACTIVE=prod

# Configurações do PostgreSQL
POSTGRES_PORT=5432
POSTGRES_DB=marketplacedb
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres

# Configurações do PgAdmin
PGADMIN_PORT=5050
PGADMIN_EMAIL=admin@marketplace.com
PGADMIN_PASSWORD=admin
```

### Personalizando o docker-compose.yml

Você pode personalizar o arquivo `docker-compose.yml` para ajustar:

- Limites de recursos (CPU e memória) para cada serviço
- Políticas de reinicialização
- Configurações de rede
- Volumes adicionais
- Variáveis de ambiente específicas

## 🛑 Parando a aplicação

```bash
# Para parar mantendo os volumes
docker-compose down

# Para parar e remover volumes (apaga dados do banco)
docker-compose down -v
```

## 🔄 Reconstruindo após Alterações

```bash
# Reconstruir apenas a imagem da aplicação
docker-compose build app

# Reiniciar apenas a aplicação
docker-compose up -d --no-deps app
```

## 📊 Monitoramento e Diagnóstico

```bash
# Verificar status dos containers
docker-compose ps

# Verificar logs da aplicação
docker-compose logs -f app

# Verificar logs do banco de dados
docker-compose logs -f db

# Acessar o shell do container da aplicação
docker-compose exec app sh
```

## 🧪 Ambiente de Desenvolvimento vs. Produção

Esta configuração é adequada para ambientes de desenvolvimento e testes. Para produção, considere:

1. Usar Docker Secrets ou Kubernetes Secrets para senhas e chaves sensíveis
2. Configurar backups automáticos do banco de dados com ferramentas como Barman ou pgBackRest
3. Implementar monitoramento e alertas com Prometheus, Grafana e Alertmanager
4. Ajustar as configurações de performance do PostgreSQL para o hardware disponível
5. Configurar HTTPS com certificados válidos usando um proxy reverso como Nginx ou Traefik
6. Implementar um sistema de log centralizado como ELK Stack ou Graylog
7. Configurar replicação do banco de dados para alta disponibilidade

## 🔒 Segurança

Para ambientes de produção, lembre-se de:

1. Alterar todas as senhas padrão e usar senhas fortes geradas aleatoriamente
2. Restringir o acesso às portas expostas usando grupos de segurança ou firewalls
3. Configurar firewalls adequadamente para permitir apenas o tráfego necessário
4. Manter as imagens Docker atualizadas com as últimas correções de segurança
5. Executar verificações de vulnerabilidades nas imagens com ferramentas como Trivy ou Clair
6. Implementar políticas de rede para limitar a comunicação entre containers
7. Configurar o Docker para executar containers com privilégios mínimos
8. Utilizar imagens base oficiais e confiáveis
9. Implementar autenticação e autorização adequadas para todos os serviços

## 🔧 Solução de problemas

### A aplicação não conecta ao banco de dados

Verifique se o container do PostgreSQL está em execução:

```bash
docker-compose ps db
```

Verifique os logs do banco de dados:

```bash
docker-compose logs db
```

### Erro ao construir a imagem Docker

Se o script `mvnw` não tiver permissão de execução, você pode executar:

```bash
chmod +x mvnw
```

E então reconstruir a imagem:

```bash
docker-compose build app
```

### Problemas de performance

Se a aplicação estiver lenta, verifique o uso de recursos dos containers:

```bash
docker stats
```

Considere ajustar os limites de memória e CPU no arquivo `docker-compose.yml`:

```yaml
services:
  app:
    deploy:
      resources:
        limits:
          cpus: '0.5'  # Limita a 50% de um núcleo de CPU
          memory: 512M # Limita a 512MB de memória
```

## 🔍 Detalhes da Configuração

### Dockerfile

O Dockerfile utiliza uma abordagem multi-estágio para otimizar o tamanho da imagem final:

1. **Estágio de build**: Utiliza Eclipse Temurin JDK 21 Alpine para compilar a aplicação
2. **Estágio de execução**: Utiliza Eclipse Temurin JRE 21 Alpine para executar a aplicação

### .dockerignore

O arquivo `.dockerignore` exclui arquivos desnecessários durante o build da imagem Docker:

- Arquivos e diretórios do Maven (exceto o JAR final)
- Arquivos de IDE (.idea, .vscode, etc.)
- Logs e arquivos temporários
- Arquivos do sistema (.DS_Store, etc.)

Isso resulta em builds mais rápidos e imagens mais leves.

## 🚀 Escalabilidade e Orquestração

Para ambientes de produção com alta demanda, considere as seguintes estratégias de escalabilidade:

### Escalabilidade Horizontal

- Utilize Kubernetes para orquestração de containers
- Configure auto-scaling baseado em métricas de CPU/memória
- Implemente balanceamento de carga com Ingress Controllers
- Utilize StatefulSets para o PostgreSQL com replicação

### Estratégias de Deployment

- Implemente CI/CD com Jenkins, GitLab CI ou GitHub Actions
- Utilize estratégias de deployment como Blue/Green ou Canary
- Configure health checks e readiness probes
- Implemente circuit breakers e retry patterns

### Persistência e Cache

- Utilize volumes persistentes gerenciados para dados críticos
- Implemente Redis ou Memcached para caching
- Configure connection pooling otimizado
- Utilize CDN para conteúdo estático

## 📚 Recursos Adicionais

- [Documentação oficial do Docker](https://docs.docker.com/)
- [Documentação do Docker Compose](https://docs.docker.com/compose/)
- [Melhores práticas para Docker em produção](https://docs.docker.com/develop/dev-best-practices/)
- [Documentação do PostgreSQL](https://www.postgresql.org/docs/)
- [Documentação do Spring Boot com Docker](https://spring.io/guides/topicals/spring-boot-docker/)