# 🔧 Guia de Solução de Problemas do Docker

## 🚨 Problemas Comuns e Soluções

### 1. Docker Desktop não está em execução

**Sintoma:** Mensagem de erro como `error during connect: Get "http://%2F%2F.%2Fpipe%2FdockerDesktopLinuxEngine/..."` ou `O sistema não pode encontrar o arquivo especificado`.

**Solução:**
1. Verifique se o Docker Desktop está instalado e em execução
2. Procure o ícone do Docker na barra de tarefas do Windows
3. Se não estiver presente, inicie o Docker Desktop pelo menu Iniciar
4. Aguarde até que o ícone do Docker na barra de tarefas fique estável (sem animação)
5. Execute o script `docker-run.bat` novamente

### 2. Portas em uso

**Sintoma:** Erro indicando que as portas 8080, 5432 ou 5050 já estão em uso.

**Solução:**
1. Identifique quais aplicações estão usando essas portas:
   ```
   netstat -ano | findstr :8080
   netstat -ano | findstr :5432
   netstat -ano | findstr :5050
   ```
2. Encerre essas aplicações ou altere as portas no arquivo `.env`

### 3. Problemas de permissão

**Sintoma:** Erros de acesso negado ou permissão insuficiente.

**Solução:**
1. Execute o Prompt de Comando ou PowerShell como Administrador
2. Navegue até o diretório do projeto e execute o script novamente

### 4. Problemas com o WSL 2

**Sintoma:** Erros relacionados ao WSL 2 ou ao backend do Docker.

**Solução:**
1. Verifique se o WSL 2 está instalado e configurado corretamente:
   ```
   wsl --status
   ```
2. Reinicie o serviço WSL:
   ```
   wsl --shutdown
   ```
3. Reinicie o Docker Desktop

### 5. Imagens não encontradas

**Sintoma:** Erro `unable to get image 'postgres:15-alpine'`.

**Solução:**
1. Verifique sua conexão com a internet
2. Tente baixar a imagem manualmente:
   ```
   docker pull postgres:15-alpine
   ```
3. Se o problema persistir, verifique se há problemas com o Docker Hub ou use um mirror alternativo

## 🔄 Reiniciando o Docker do Zero

Se os problemas persistirem, tente reiniciar completamente o ambiente Docker:

1. Pare todos os containers em execução:
   ```
   docker stop $(docker ps -q)
   ```

2. Remova todos os containers parados:
   ```
   docker rm $(docker ps -a -q)
   ```

3. Reinicie o Docker Desktop (clique com o botão direito no ícone da barra de tarefas e selecione "Restart")

4. Aguarde a inicialização completa e tente novamente

## 📋 Verificando o Status do Docker

Para verificar se o Docker está funcionando corretamente:

```
docker info
docker version
docker-compose version
```

Todos esses comandos devem retornar informações sem erros.

## 🔍 Logs e Diagnóstico

Para obter mais informações sobre problemas:

1. Verifique os logs do Docker Desktop:
   - Clique no ícone do Docker na barra de tarefas
   - Selecione "Troubleshoot" ou "Diagnostics"

2. Verifique os logs dos containers:
   ```
   docker-compose logs
   ```

3. Verifique os logs específicos de cada serviço:
   ```
   docker-compose logs app
   docker-compose logs db
   docker-compose logs pgadmin
   ```

## 🆘 Suporte Adicional

Se os problemas persistirem após tentar as soluções acima:

1. Consulte a [documentação oficial do Docker](https://docs.docker.com/desktop/troubleshoot/overview/)
2. Verifique o [fórum da comunidade Docker](https://forums.docker.com/)
3. Procure por problemas semelhantes no [Stack Overflow](https://stackoverflow.com/questions/tagged/docker)

## 🔒 Problemas de Segurança

Se você estiver enfrentando problemas relacionados a permissões ou segurança:

1. Verifique se sua conta de usuário tem permissões para executar o Docker
2. No Windows, certifique-se de que sua conta é membro do grupo "docker-users"
3. Considere executar o Docker Desktop como administrador para diagnóstico

---

*Este guia é específico para a configuração do projeto Sales API. Para problemas mais gerais do Docker, consulte a documentação oficial.*