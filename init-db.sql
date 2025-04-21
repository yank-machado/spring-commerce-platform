-- Script de inicialização do banco de dados PostgreSQL

-- Criar extensões úteis
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- Configurar permissões para o usuário da aplicação
ALTER USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE marketplacedb TO postgres;

-- Configurações de performance
ALTER SYSTEM SET max_connections = '100';
ALTER SYSTEM SET shared_buffers = '256MB';
ALTER SYSTEM SET effective_cache_size = '1GB';
ALTER SYSTEM SET maintenance_work_mem = '64MB';
ALTER SYSTEM SET checkpoint_completion_target = '0.9';
ALTER SYSTEM SET wal_buffers = '16MB';
ALTER SYSTEM SET default_statistics_target = '100';
ALTER SYSTEM SET random_page_cost = '4';
ALTER SYSTEM SET effective_io_concurrency = '2';
ALTER SYSTEM SET work_mem = '4MB';

-- Comentário: Estas configurações são para um ambiente de desenvolvimento/teste
-- Para produção, ajuste os valores de acordo com os recursos disponíveis