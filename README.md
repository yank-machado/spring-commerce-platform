```markdown
# 🛍️ Sales API - Plataforma de Marketplace Completa

**Uma solução robusta para e-commerce com gestão de lojas, produtos, pedidos e usuários**

![Badges](https://img.shields.io/badge/Java-17+-orange?logo=openjdk)
![Badges](https://img.shields.io/badge/Spring%20Boot-3.0-blue?logo=springboot)
![Badges](https://img.shields.io/badge/PostgreSQL-15+-blue?logo=postgresql)
![License](https://img.shields.io/badge/License-MIT-green)

## 🌟 Visão Geral

A Sales API é uma plataforma completa de marketplace desenvolvida com as melhores práticas de arquitetura de software. Oferece recursos essenciais para:

- 🏪 Criação e gestão de lojas virtuais
- 📦 Gerenciamento de catálogos de produtos
- 🛒 Processamento de pedidos e pagamentos
- 👥 Controle de usuários e permissões
- 🔒 Autenticação segura com JWT

## 🛠 Tecnologias Utilizadas

### Backend
- **Java 17** - Linguagem principal
- **Spring Boot 3** - Framework base
- **Spring Data JPA** - Persistência de dados
- **Hibernate** - ORM
- **PostgreSQL** - Banco de dados relacional
- **Spring Security** - Autenticação e autorização

### Ferramentas
- **Maven** - Gerenciamento de dependências
- **Docker** - Containerização (opcional)
- **Postman** - Testes de API

## 🚀 Começando

### Pré-requisitos
- JDK 17+
- Maven 3.8+
- PostgreSQL 15+

### ⚙️ Configuração

1. **Clonar repositório**
   ```bash
   git clone https://github.com/seu-usuario/sales-api.git
   cd sales-api
   ```

2. **Configurar banco de dados**
   ```properties
   # application.properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/sales_db
   spring.datasource.username=postgres
   spring.datasource.password=senha_segura
   ```

### ▶️ Executando a Aplicação
```bash
mvn clean install
mvn spring-boot:run
```

Acesse a documentação:  
`http://localhost:8080/swagger-ui.html`

## 📂 Estrutura do Projeto
```
src/
├── main/
│   ├── java/
│   │   └── com/sales-api/
│   │       ├── config/
│   │       ├── controller/
│   │       ├── dto/
│   │       └── service/
│   └── resources/
│       └── db/migration/
```

## 🔑 Autenticação

### Fluxo de Autenticação JWT
1. POST `/api/auth/login` com credenciais
2. Receber token JWT na resposta
3. Usar token em requisições subsequentes

**Exemplo de requisição:**
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "senhaSegura123"
}
```

## 📚 Documentação da API

Endpoint | Descrição
---|---
`GET /api/products` | Listar todos os produtos
`POST /api/orders` | Criar novo pedido
`PUT /api/users/{id}` | Atualizar usuário

## 💡 Funcionalidades Principais

### Módulo de Usuários 👤
```http
POST /api/users
Content-Type: application/json

{
  "username": "novo_user",
  "password": "senha123",
  "email": "user@example.com"
}
```

### Módulo de Pedidos 🛒
**Estados do pedido:**
```
PENDENTE → PAGO → EM_TRANSITO → ENTREGUE
           ↳ CANCELADO
```

## 🛡️ Modelo de Segurança

Role | Permissões
---|---
USER | Gerenciar próprio perfil/pedidos
SELLER | Gerenciar lojas/produtos
ADMIN | Acesso completo ao sistema


**Desenvolvido por [Yank Machado]**  
[@yank-machado] (https://github.com/yank-machado)
```