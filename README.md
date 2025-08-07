# QuantoTeDevoBackEnd

API REST para gerenciamento de dívidas, contas compartilhadas e compras em grupo.

## 🚀 Como executar

```bash
./mvnw spring-boot:run
```

O servidor estará disponível em `http://localhost:8080`

## 📋 Funcionalidades

- **RF01**: Cadastro de usuários
- **RF02**: Listagem de usuários
- **RF03**: Criação de contas
- **RF04**: Divisão de contas entre usuários
- **RF05**: Listagem de contas
- **RF06**: Criação de dívidas diretas
- **RF07**: Listagem de dívidas
- **RF08**: Pagamento de dívidas
- **RF09**: Visualização de saldo devedor/credor
- **RF10**: Histórico de transações
- **RF13**: Compras com múltiplos itens

## 🔗 Documentação das APIs

### � Autenticação (`/auth`)

#### **POST** `/auth/register`
Registra um novo usuário e retorna token JWT.

**Request Body:**
```json
{
  "nome": "João Silva",
  "email": "joao@email.com",
  "senha": "senha123",
  "chavePix": "joao@pix.com"
}
```

**Response:**
```json
{
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5...",
    "usuario": {
      "id": 1,
      "nome": "João Silva",
      "email": "joao@email.com",
      "chavePix": "joao@pix.com"
    }
  },
  "message": "Usuário registrado com sucesso"
}
```

#### **POST** `/auth/login`
Autentica um usuário e retorna token JWT.

**Request Body:**
```json
{
  "email": "joao@email.com",
  "senha": "senha123"
}
```

**Response:**
```json
{
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5...",
    "usuario": {
      "id": 1,
      "nome": "João Silva",
      "email": "joao@email.com",
      "chavePix": "joao@pix.com"
    }
  },
  "message": "Login realizado com sucesso"
}
```

#### **POST** `/auth/logout`
Realiza logout (no frontend, remove o token do localStorage).

**Response:**
```json
{
  "data": null,
  "message": "Logout realizado com sucesso"
}
```

#### **POST** `/auth/refresh`
Renova um token JWT que está expirando.

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5...
```

**Response:**
```json
{
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5_NEW_TOKEN..."
  },
  "message": "Token renovado com sucesso"
}
```

---

### �👤 Usuários (`/api/usuarios`)

**⚠️ Nota:** Todas as rotas `/api/**` agora requerem autenticação com Bearer Token.

**Headers obrigatórios:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5...
```

#### **POST** `/api/usuarios`
Cria um novo usuário.

**Request Body:**
```json
{
  "nome": "João Silva",
  "email": "joao@email.com",
  "chavePix": "joao@pix.com"
}
```

**Response:**
```json
{
  "id": 1,
  "nome": "João Silva",
  "email": "joao@email.com",
  "chavePix": "joao@pix.com"
}
```

#### **GET** `/api/usuarios`
Lista todos os usuários.

**Response:**
```json
[
  {
    "id": 1,
    "nome": "João Silva",
    "email": "joao@email.com",
    "chavePix": "joao@pix.com"
  },
  {
    "id": 2,
    "nome": "Maria Santos",
    "email": "maria@email.com",
    "chavePix": "maria@pix.com"
  }
]
```

#### **GET** `/api/usuarios/{id}`
Busca um usuário por ID.

**Response:**
```json
{
  "id": 1,
  "nome": "João Silva",
  "email": "joao@email.com",
  "chavePix": "joao@pix.com"
}
```

#### **PUT** `/api/usuarios/{id}`
Atualiza um usuário.

**Request Body:**
```json
{
  "nome": "João Silva Santos",
  "email": "joao.santos@email.com",
  "chavePix": "joao.santos@pix.com"
}
```

#### **DELETE** `/api/usuarios/{id}`
Remove um usuário.

**Response:** `204 No Content`

---

### 💰 Contas (`/api/contas`)

#### **POST** `/api/contas`
Cria uma nova conta.

**Request Body:**
```json
{
  "descricao": "Jantar no restaurante",
  "valor": 120.50,
  "vencimento": "2025-08-15",
  "criadorId": 1
}
```

**Response:**
```json
{
  "id": 1,
  "descricao": "Jantar no restaurante",
  "valor": 120.50,
  "vencimento": "2025-08-15",
  "criador": {
    "id": 1,
    "nome": "João Silva",
    "email": "joao@email.com",
    "chavePix": "joao@pix.com"
  },
  "paga": false,
  "dataCriacao": "2025-08-06T20:30:00",
  "divisoes": []
}
```

#### **GET** `/api/contas`
Lista todas as contas.

**Query Parameters:**
- `paga` (opcional): `true` ou `false` para filtrar por status de pagamento

**Response:**
```json
[
  {
    "id": 1,
    "descricao": "Jantar no restaurante",
    "valor": 120.50,
    "vencimento": "2025-08-15",
    "criador": {
      "id": 1,
      "nome": "João Silva",
      "email": "joao@email.com",
      "chavePix": "joao@pix.com"
    },
    "paga": false,
    "dataCriacao": "2025-08-06T20:30:00",
    "divisoes": []
  }
]
```

#### **GET** `/api/contas/{id}`
Busca uma conta por ID.

#### **PUT** `/api/contas/{id}`
Atualiza uma conta.

#### **DELETE** `/api/contas/{id}`
Remove uma conta.

#### **GET** `/api/contas/usuario/{usuarioId}`
Lista contas de um usuário específico.

**Query Parameters:**
- `paga` (opcional): `true` ou `false`

---

### 📊 Divisões (`/api/divisoes`)

#### **POST** `/api/divisoes`
Cria uma nova divisão de conta.

**Request Body:**
```json
{
  "contaId": 1,
  "usuarioId": 2,
  "valor": 60.25
}
```

#### **GET** `/api/divisoes/conta/{contaId}`
Lista divisões de uma conta específica.

#### **GET** `/api/divisoes/usuario/{usuarioId}`
Lista divisões de um usuário específico.

**Query Parameters:**
- `pago` (opcional): `true` ou `false`

#### **PATCH** `/api/divisoes/{id}/pagar`
Marca uma divisão como paga.

**Request Body:**
```json
{
  "formaPagamento": "PIX"
}
```

---

### 💳 Dívidas (`/api/dividas`)

#### **POST** `/api/dividas`
Cria uma nova dívida direta.

**Request Body:**
```json
{
  "descricao": "Empréstimo para almoço",
  "valor": 25.00,
  "usuarioDevedorId": 2,
  "usuarioCredorId": 1,
  "dataVencimento": "2025-08-20"
}
```

**Response:**
```json
{
  "id": 1,
  "descricao": "Empréstimo para almoço",
  "valor": 25.00,
  "usuarioDevedor": {
    "id": 2,
    "nome": "Maria Santos",
    "email": "maria@email.com",
    "chavePix": "maria@pix.com"
  },
  "usuarioCredor": {
    "id": 1,
    "nome": "João Silva",
    "email": "joao@email.com",
    "chavePix": "joao@pix.com"
  },
  "dataCriacao": "2025-08-06T20:35:00",
  "dataVencimento": "2025-08-20",
  "paga": false,
  "dataPagamento": null,
  "formaPagamento": null
}
```

#### **GET** `/api/dividas`
Lista todas as dívidas.

**Query Parameters:**
- `paga` (opcional): `true` ou `false`

#### **GET** `/api/dividas/{id}`
Busca uma dívida por ID.

#### **GET** `/api/dividas/usuario/{usuarioId}/devendo`
Lista dívidas onde o usuário é devedor.

**Query Parameters:**
- `paga` (opcional): `true` ou `false`

#### **GET** `/api/dividas/usuario/{usuarioId}/recebendo`
Lista dívidas onde o usuário é credor.

**Query Parameters:**
- `paga` (opcional): `true` ou `false`

#### **GET** `/api/dividas/usuario/{usuarioId}/saldo`
Retorna o saldo devedor/credor do usuário.

**Response:**
```json
{
  "usuarioId": 1,
  "totalDevendo": 50.00,
  "totalRecebendo": 75.00,
  "saldoLiquido": 25.00
}
```

#### **PATCH** `/api/dividas/{id}/pagar`
Marca uma dívida como paga.

**Request Body:**
```json
{
  "formaPagamento": "PIX"
}
```

#### **DELETE** `/api/dividas/{id}`
Remove uma dívida.

---

### 🛒 Compras (`/api/compras`)

#### **POST** `/api/compras`
Cria uma nova compra com múltiplos itens.

**Request Body:**
```json
{
  "descricao": "Compras do Supermercado",
  "usuarioCriadorId": 1,
  "observacoes": "Compras da semana",
  "itens": [
    {
      "descricao": "Arroz 5kg",
      "valor": 25.90,
      "quantidade": 2,
      "usuarioResponsavelId": 1,
      "observacoes": "Marca premium"
    },
    {
      "descricao": "Feijão 1kg",
      "valor": 8.50,
      "quantidade": 3,
      "usuarioResponsavelId": 2,
      "observacoes": "Feijão carioca"
    },
    {
      "descricao": "Óleo de Soja 900ml",
      "valor": 4.75,
      "quantidade": 1,
      "usuarioResponsavelId": 3,
      "observacoes": "Marca Liza"
    }
  ]
}
```

**Response:**
```json
{
  "id": 1,
  "descricao": "Compras do Supermercado",
  "dataCompra": "2025-08-06",
  "dataCriacao": "2025-08-06T20:40:00",
  "usuarioCriador": {
    "id": 1,
    "nome": "João Silva",
    "email": "joao@email.com",
    "chavePix": "joao@pix.com"
  },
  "itens": [
    {
      "id": 1,
      "descricao": "Arroz 5kg",
      "valor": 25.90,
      "quantidade": 2,
      "usuarioResponsavel": {
        "id": 1,
        "nome": "João Silva"
      },
      "observacoes": "Marca premium",
      "valorTotal": 51.80
    }
  ],
  "finalizada": false,
  "observacoes": "Compras da semana",
  "valorTotal": 87.05
}
```

#### **GET** `/api/compras/{id}`
Busca uma compra por ID.

#### **GET** `/api/compras/usuario/{usuarioId}/criadas`
Lista compras criadas por um usuário.

**Query Parameters:**
- `finalizada` (opcional): `true` ou `false`

#### **GET** `/api/compras/usuario/{usuarioId}/participando`
Lista compras onde o usuário tem itens responsáveis.

**Query Parameters:**
- `ativas` (opcional): `true` ou `false` (padrão: `true` - apenas compras não finalizadas)

#### **PATCH** `/api/compras/{id}/finalizar`
Finaliza uma compra e gera dívidas automaticamente.

**Response:**
```json
{
  "id": 1,
  "descricao": "Compras do Supermercado",
  "finalizada": true,
  "dividasGeradas": 2
}
```

#### **POST** `/api/compras/{id}/itens`
Adiciona um novo item a uma compra existente.

**Request Body:**
```json
{
  "descricao": "Leite 1L",
  "valor": 4.50,
  "quantidade": 2,
  "usuarioResponsavelId": 2,
  "observacoes": "Leite integral"
}
```

#### **DELETE** `/api/compras/{id}`
Remove uma compra (apenas se não finalizada).

---

### 📈 Relatórios e Saldos

#### **GET** `/api/usuarios/{usuarioId}/historico`
Retorna histórico completo de transações do usuário.

**Response:**
```json
{
  "usuarioId": 1,
  "contasCriadas": 5,
  "divisoesPendentes": 3,
  "divisoesPagas": 7,
  "dividasDevendo": 2,
  "dividasRecebendo": 4,
  "comprasCriadas": 3,
  "comprasParticipando": 8,
  "saldoTotal": -25.50
}
```

## 🛠️ Tecnologias

- **Java 17**
- **Spring Boot 3.5.4**
- **Spring Data JPA**
- **H2 Database** (desenvolvimento)
- **Maven**
- **Lombok**

## 🗃️ Banco de Dados

O projeto utiliza H2 Database em memória para desenvolvimento. Para acessar o console do H2:

1. Acesse: `http://localhost:8080/h2-console`
2. JDBC URL: `jdbc:h2:mem:testdb`
3. Username: `sa`
4. Password: (deixe em branco)

### Estrutura das Tabelas

- **usuarios**: id, nome, email, chave_pix
- **contas**: id, descricao, valor, vencimento, criador_id, paga, data_criacao
- **divisoes**: id, conta_id, usuario_id, valor, pago, data_pagamento, forma_pagamento
- **dividas**: id, descricao, valor, usuario_devedor_id, usuario_credor_id, data_criacao, data_vencimento, paga, data_pagamento, forma_pagamento
- **compras**: id, descricao, data_compra, data_criacao, usuario_criador_id, finalizada, observacoes
- **itens_compra**: id, compra_id, descricao, valor, quantidade, usuario_responsavel_id, observacoes

## 🧪 Exemplos de Teste

### Cenário Completo: Jantar em Grupo

1. **Criar usuários:**
```bash
curl -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{"nome": "Ana", "email": "ana@email.com", "chavePix": "ana@pix.com"}'
```

2. **Criar compra:**
```bash
curl -X POST http://localhost:8080/api/compras \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "Jantar Pizzaria",
    "usuarioCriadorId": 1,
    "itens": [
      {"descricao": "Pizza Grande", "valor": 45.00, "quantidade": 2, "usuarioResponsavelId": 1},
      {"descricao": "Refrigerante 2L", "valor": 8.00, "quantidade": 3, "usuarioResponsavelId": 2}
    ]
  }'
```

3. **Finalizar compra (gerar dívidas):**
```bash
curl -X PATCH http://localhost:8080/api/compras/1/finalizar
```

4. **Verificar dívidas geradas:**
```bash
curl -X GET http://localhost:8080/api/dividas/usuario/2/devendo
```

## 📝 Status Codes

- **200 OK**: Sucesso
- **201 Created**: Recurso criado
- **204 No Content**: Sucesso sem retorno
- **400 Bad Request**: Erro de validação
- **404 Not Found**: Recurso não encontrado
- **500 Internal Server Error**: Erro interno