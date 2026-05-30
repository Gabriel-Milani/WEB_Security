# Entrega 1 - Web Security

Este repositorio contem os dois microsservicos da Etapa 1:

- `secrest`: User Service com Spring Security, JWT e MySQL (`ms_user`) na porta `8081`.
- `email`: Email Service base com Web, JPA, MySQL, Validation, AMQP e Mail (`ms_email`) na porta `8082`, sem consumer ativo nesta etapa.

## Validacao

```powershell
cd secrest
.\mvnw.cmd test

cd ..\email
.\mvnw.cmd test
```
