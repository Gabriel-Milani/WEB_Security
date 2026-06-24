# WEB Security - Entrega Final

Projeto com tres partes:

- `secrest`: User Service com Spring Security, JWT, OTP em cache, MySQL e producer RabbitMQ.
- `email`: Email Service com consumer RabbitMQ, envio por Gmail SMTP e persistencia em MySQL.
- `frontend`: aplicacao Node.js/Express com fluxo de e-mail, codigo, cadastro de perfil e dashboard.

## Arquitetura

1. O usuario informa o e-mail no frontend.
2. O frontend chama `POST /auth/request-code` no User Service.
3. O User Service gera um codigo de 6 digitos, guarda em cache por 5 minutos e publica uma mensagem na fila `default.email`.
4. O Email Service consome a fila, envia o e-mail real e salva o resultado no banco `ms_email`.
5. O usuario informa o codigo no frontend.
6. O User Service valida o codigo e devolve um JWT.
7. O frontend salva o token em `sessionStorage`, solicita nome/cargo e chama endpoints protegidos com `Authorization: Bearer`.

## Pre-requisitos

- JDK 17+.
- Node.js 18+.
- Maven Wrapper ja incluido nos projetos Java.
- MySQL 8.
- Conta CloudAMQP com URI AMQP.
- Conta Gmail com senha de aplicativo.

## Bancos

Crie os bancos:

```sql
CREATE DATABASE IF NOT EXISTS ms_user;
CREATE DATABASE IF NOT EXISTS ms_email;
```

## Configuracao

User Service: `secrest/src/main/resources/application.properties`

```properties
server.port=8081
spring.datasource.url=jdbc:mysql://localhost:3306/ms_user?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=SUA_SENHA
spring.rabbitmq.addresses=SUA_URI_CLOUDAMQP
broker.queue.email.name=default.email
```

Email Service: `email/src/main/resources/application.properties`

```properties
server.port=8082
spring.datasource.url=jdbc:mysql://localhost:3306/ms_email?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=SUA_SENHA
spring.rabbitmq.addresses=SUA_URI_CLOUDAMQP
broker.queue.email.name=default.email
spring.mail.username=${GMAIL_USERNAME:}
spring.mail.password=${GMAIL_APP_PASSWORD:}
```

Antes de iniciar o Email Service:

```powershell
$env:GMAIL_USERNAME="seuemail@gmail.com"
$env:GMAIL_APP_PASSWORD="sua_senha_de_app"
```

## Como executar

Terminal 1:

```powershell
cd secrest
.\mvnw.cmd spring-boot:run
```

Terminal 2:

```powershell
cd email
.\mvnw.cmd spring-boot:run
```

Terminal 3:

```powershell
cd frontend
npm install
npm start
```

Ou execute:

```powershell
.\iniciar.ps1
```

## Fluxo de teste

1. Acesse `http://localhost:3000`.
2. Informe um e-mail real.
3. Abra o Gmail e copie o codigo recebido.
4. Informe o codigo na pagina de verificacao.
5. Preencha nome e cargo.
6. No dashboard, teste os botoes `Testar endpoint protegido`, `Meu perfil` e `Sair`.

## Endpoints principais

- `POST /auth/request-code`
- `POST /auth/verify-code`
- `POST /users/update-profile`
- `GET /users/me`
- `GET /users/test/customer`

## Prints da entrega

Inclua capturas de tela de:

- E-mail recebido no Gmail com o codigo de 6 digitos.
- Tela de verificacao do codigo.
- Tela de cadastro de nome/cargo.
- Dashboard exibindo o perfil.
- RabbitMQ Manager mostrando a fila `default.email`.
