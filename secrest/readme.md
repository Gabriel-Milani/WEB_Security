Testando a API
10.1. Inicie a aplicação
mvn spring-boot:run

10.2. Crie um usuário com papel ROLE_CUSTOMER
Endpoint: POST http://localhost:8080/users=
{
 "email": "maria@email.com",
 "password": "123456",
 "role": "ROLE_CUSTOMER"
}
✅ Resposta esperada: 201 CREATED
10.3. Faça login
Endpoint: POST http://localhost:8080/users/login
{
 "email": "maria@email.com",
 "password": "123456"
}
✅ Resposta esperada (exemplo):
{ "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." }
10.4. Acesse um endpoint protegido
Adicione o header Authorization com o valor Bearer SEU_TOKEN_JWT.
Endpoint CUSTOMER: GET http://localhost:8080/users/test/customer → ✅ acesso liberado
Endpoint ADMIN: GET http://localhost:8080/users/test/administrator → ❌ retorna 403
Forbidden
10.5. Crie um usuário ADMIN e repita os testes
Basta alterar o role para ROLE_ADMINISTRATOR no cadastro. O usuário administrador terá acesso a ambos os
endpoints restritos.