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
- **R---

### 👥 Sistema de Grupos (`/api/grupos`)

O sistema de grupos facilita a divisão recorrente de contas entre pessoas próximas (casal, república, amigos próximos).

#### **POST** `/api/grupos/criar/{criadorId}`
Cria um novo grupo.

**Request Body:**
```json
{
  "nome": "República da Ana",
  "descricao": "Grupo para dividir contas da república",
  "membrosIniciais": [2, 3]
}
```

**Response:**
```json
{
  "id": 1,
  "nome": "República da Ana",
  "descricao": "Grupo para dividir contas da república",
  "criador": {
    "id": 1,
    "nome": "Ana",
    "email": "ana@email.com",
    "chavePix": "ana@pix.com"
  },
  "membros": [
    {
      "id": 2,
      "nome": "Bruno",
      "email": "bruno@email.com",
      "chavePix": "11987654321"
    },
    {
      "id": 3,
      "nome": "Carlos",
      "email": "carlos@email.com",
      "chavePix": "carlos@email.com"
    }
  ],
  "dataCriacao": "2025-08-07T01:26:08.784924608",
  "ativo": true,
  "totalMembros": 2,
  "totalContas": 0,
  "usuarioECriador": true,
  "usuarioEMembro": false
}
```

#### **GET** `/api/grupos/usuario/{usuarioId}`
Lista todos os grupos do usuário (criados ou onde é membro).

**Response:**
```json
[
  {
    "id": 1,
    "nome": "República da Ana",
    "descricao": "Grupo para dividir contas da república",
    "criador": {
      "id": 1,
      "nome": "Ana",
      "email": "ana@email.com",
      "chavePix": "ana@pix.com"
    },
    "membros": [...],
    "dataCriacao": "2025-08-07T01:26:08.784924",
    "ativo": true,
    "totalMembros": 2,
    "totalContas": 5,
    "usuarioECriador": false,
    "usuarioEMembro": true
  }
]
```

#### **GET** `/api/grupos/{grupoId}/usuario/{usuarioId}`
Busca detalhes de um grupo específico.

#### **PUT** `/api/grupos/{grupoId}/membros/{usuarioId}`
Adiciona ou remove membros do grupo (apenas criador).

**Request Body:**
```json
{
  "usuarioIds": [4, 5],
  "acao": "ADICIONAR"
}
```

**Ações disponíveis:**
- `ADICIONAR` - Adiciona novos membros
- `REMOVER` - Remove membros existentes

#### **DELETE** `/api/grupos/{grupoId}/desativar/{usuarioId}`
Desativa um grupo (apenas criador).

#### **DELETE** `/api/grupos/{grupoId}/sair/{usuarioId}`
Remove usuário do grupo (membros podem sair, criador não).

#### **GET** `/api/contas/grupo/{grupoId}`
Lista contas associadas a um grupo.

**Query Parameters:**
- `paga` (opcional): `true` ou `false`

### Regras de Negócio - Grupos

- ✅ **Apenas amigos podem ser adicionados ao grupo**
- ✅ **Criador tem controle total do grupo**
- ✅ **Membros podem sair do grupo**
- ✅ **Criador não pode sair (deve desativar ou transferir)**
- ✅ **Grupos desativados mantêm histórico**
- ✅ **Validação de permissões em todas as operações**

### Funcionalidades do Sistema de Grupos

✅ **Implementado e Testado:**
- Criação de grupos com membros iniciais
- Listagem de grupos do usuário
- Busca detalhada de grupo
- Adição/remoção de membros
- Saída do grupo
- Desativação de grupos
- Associação de contas a grupos
- Validações de amizade e permissões

---

### 👥 Sistema de Amizades (`/api/amizades`)**: Visualização de saldo devedor/credor
- **RF10**: Histórico de transações
- **RF11**: Sistema de amizades (solicitação, aceitação, listagem)
- **RF12**: Bloqueio e remoção de amigos
- **RF13**: Compras com múltiplos itens
- **RF14**: Sistema de grupos para divisão recorrente
- **RF15**: Gerenciamento de membros de grupos
- **RF16**: Divisão automática por porcentagem
- **RF17**: Marcar contas como vencidas manualmente

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

##### Status das Contas
As contas possuem os seguintes status possíveis:
- **PENDENTE**: Conta criada mas ainda não paga nem vencida
- **PAGA**: Conta foi marcada como paga
- **VENCIDA**: Conta foi marcada como vencida (manualmente ou automaticamente)
- **PARCIALMENTE_PAGA**: Conta teve apenas parte do valor pago (uso futuro)

#### **GET** `/api/contas/{id}`
Busca uma conta específica por ID.

#### **GET** `/api/contas/usuario/{usuarioId}`
Lista todas as contas relacionadas a um usuário específico (criadas por ele ou onde ele participa).

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
    "paga": false,
    "status": "PENDENTE",
    "dataCriacao": "2025-08-06T20:30:00",
    "criador": {
      "id": 1,
      "nome": "João Silva",
      "email": "joao@email.com",
      "chavePix": "joao@pix.com"
    }
  }
]
```

#### **GET** `/api/contas/usuario/{usuarioId}/filtros`
Lista contas de um usuário com filtros avançados.

**Query Parameters:**
- `paga` (opcional): `true` ou `false` para filtrar por status
- `vencimentoInicial` (opcional): Data inicial (formato: YYYY-MM-DD)
- `vencimentoFinal` (opcional): Data final (formato: YYYY-MM-DD)

#### **GET** `/api/contas/usuario/{usuarioId}/vencidas`
Lista apenas as contas vencidas de um usuário específico.

#### **GET** `/api/contas/grupo/{grupoId}`
Lista contas de um grupo específico.

**Query Parameters:**
- `paga` (opcional): `true` ou `false` para filtrar por status

#### **PUT** `/api/contas/{id}`
Atualiza uma conta.

#### **DELETE** `/api/contas/{id}`
Remove uma conta.

#### **PATCH** `/api/contas/{id}/marcar-paga`
Marca uma conta como paga.

**Response:**
```json
{
  "id": 1,
  "descricao": "Jantar no restaurante",
  "valor": 120.50,
  "vencimento": "2025-08-15",
  "paga": true,
  "status": "PAGA",
  "dataCriacao": "2025-08-06T20:30:00",
  "criador": {...}
}
```

#### **PATCH** `/api/contas/{id}/marcar-vencida` 🆕
Marca uma conta como vencida manualmente (RF17).

**Comportamento:**
- Só funciona para contas não pagas (status PENDENTE)
- Contas já pagas retornam erro 400
- Útil para marcar contas que já passaram do vencimento

**Response (Sucesso):**
```json
{
  "id": 1,
  "descricao": "Conta de teste para vencimento",
  "valor": 150.00,
  "vencimento": "2025-08-10",
  "paga": false,
  "status": "VENCIDA",
  "dataCriacao": "2025-08-07T01:47:53.694682",
  "criador": {...}
}
```

**Response (Erro - conta já paga):**
```json
{
  "timestamp": "2025-08-07T01:48:35.108",
  "status": 400,
  "error": "Bad Request",
  "message": "Não é possível marcar uma conta paga como vencida"
}
```

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

#### **POST** `/api/divisoes/dividir-porcentagem`
Divide uma conta automaticamente baseada em porcentagens.

**Request Body:**
```json
{
  "contaId": 1,
  "divisoes": [
    {"usuarioId": 1, "percentual": 0.5},
    {"usuarioId": 2, "percentual": 0.3},
    {"usuarioId": 3, "percentual": 0.2}
  ]
}
```

**Response:**
```json
"Conta dividida por porcentagem com sucesso!"
```

### Regras da Divisão por Porcentagem

- ✅ **Soma dos percentuais deve ser exatamente 100% (1.0)**
- ✅ **Percentuais aceitos de 0.01 a 1.0 (1% a 100%)**
- ✅ **Até 4 casas decimais de precisão**
- ✅ **Último usuário recebe valor restante para evitar erros de arredondamento**
- ✅ **Validação se conta já possui divisões**
- ✅ **Valores calculados automaticamente baseados no valor total da conta**

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

### � Sistema de Amizades (`/api/amizades`)

O sistema de amizades permite que usuários se conectem antes de compartilhar contas e dívidas.

#### **POST** `/api/amizades/solicitar/{usuarioId}`
Solicita amizade para outro usuário.

**Request Body:**
```json
{
  "convidadoId": 2,
  "mensagem": "Vamos ser amigos!" // opcional
}
```

**Response:**
```json
{
  "id": 1,
  "solicitanteId": 1,
  "solicitante": {
    "id": 1,
    "nome": "Ana",
    "email": "ana@email.com",
    "chavePix": "ana@pix.com"
  },
  "convidadoId": 2,
  "convidado": {
    "id": 2,
    "nome": "Bruno",
    "email": "bruno@email.com",
    "chavePix": "11987654321"
  },
  "status": "PENDENTE",
  "dataSolicitacao": "2025-08-07T01:13:13.179514324",
  "dataResposta": null,
  "solicitacao": true
}
```

#### **GET** `/api/amizades/pendentes/{usuarioId}`
Lista convites de amizade pendentes recebidos pelo usuário.

**Response:**
```json
[
  {
    "id": 1,
    "solicitanteId": 1,
    "solicitante": {
      "id": 1,
      "nome": "Ana",
      "email": "ana@email.com",
      "chavePix": "ana@pix.com"
    },
    "convidadoId": 2,
    "status": "PENDENTE",
    "dataSolicitacao": "2025-08-07T01:13:13.179514",
    "solicitacao": false
  }
]
```

#### **POST** `/api/amizades/{amizadeId}/aceitar/{usuarioId}`
Aceita uma solicitação de amizade.

**Response:**
```json
{
  "id": 1,
  "solicitanteId": 1,
  "solicitante": {
    "id": 1,
    "nome": "Ana",
    "email": "ana@email.com",
    "chavePix": "ana@pix.com"
  },
  "convidadoId": 2,
  "convidado": {
    "id": 2,
    "nome": "Bruno",
    "email": "bruno@email.com",
    "chavePix": "11987654321"
  },
  "status": "ACEITA",
  "dataSolicitacao": "2025-08-07T01:13:13.179514",
  "dataResposta": "2025-08-07T01:13:19.661495469",
  "solicitacao": false
}
```

#### **POST** `/api/amizades/{amizadeId}/rejeitar/{usuarioId}`
Rejeita uma solicitação de amizade.

#### **GET** `/api/amizades/usuario/{usuarioId}`
Lista todos os amigos de um usuário.

**Response:**
```json
[
  {
    "id": 2,
    "nome": "Bruno",
    "email": "bruno@email.com",
    "chavePix": "11987654321"
  }
]
```

#### **GET** `/api/amizades/verificar/{usuario1Id}/{usuario2Id}`
Verifica se dois usuários são amigos.

**Response:**
```json
{
  "saoAmigos": true
}
```

#### **DELETE** `/api/amizades/remover/{usuario1Id}/{usuario2Id}`
Remove amizade entre dois usuários.

**Response:** `204 No Content`

#### **POST** `/api/amizades/bloquear/{bloqueadorId}/{bloqueadoId}`
Bloqueia um usuário (impede futuras solicitações).

**Response:** `204 No Content`

### Status de Amizade

- **PENDENTE**: Solicitação enviada, aguardando resposta
- **ACEITA**: Amizade estabelecida
- **REJEITADA**: Solicitação recusada
- **BLOQUEADA**: Usuário bloqueado

### Funcionalidades do Sistema de Amizades

✅ **Implementado e Testado:**
- Envio de solicitações de amizade com validações
- Listagem de convites pendentes
- Aceitação e rejeição de convites
- Listagem de amigos de um usuário
- Verificação de status de amizade entre usuários
- Remoção de amizade
- Sistema de bloqueio de usuários
- Prevenção de solicitações duplicadas
- Validação de permissões (apenas convidado pode responder)

---

### �📈 Relatórios e Saldos

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
- **contas**: id, descricao, valor, vencimento, criador_id, grupo_id, paga, data_criacao
- **divisoes**: id, conta_id, usuario_id, valor, pago, data_pagamento, forma_pagamento
- **dividas**: id, descricao, valor, usuario_devedor_id, usuario_credor_id, data_criacao, data_vencimento, paga, data_pagamento, forma_pagamento
- **compras**: id, descricao, data_compra, data_criacao, usuario_criador_id, finalizada, observacoes
- **itens_compra**: id, compra_id, descricao, valor, quantidade, usuario_responsavel_id, observacoes
- **grupos**: id, nome, descricao, criador_id, data_criacao, ativo
- **grupo_membros**: grupo_id, usuario_id (tabela de relacionamento many-to-many)
- **amizades**: id, solicitante_id, convidado_id, status, data_solicitacao, data_resposta

## 🧪 Exemplos de Teste

### Cenário Completo: Grupo de República

1. **Criar usuários e estabelecer amizades:**
```bash
# Criar usuários
curl -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{"nome": "Ana", "email": "ana@email.com", "chavePix": "ana@pix.com"}'

curl -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{"nome": "Bruno", "email": "bruno@email.com", "chavePix": "11987654321"}'

# Estabelecer amizade
curl -X POST http://localhost:8080/api/amizades/solicitar/1 \
  -H "Content-Type: application/json" \
  -d '{"convidadoId": 2}'

curl -X POST http://localhost:8080/api/amizades/1/aceitar/2
```

2. **Criar grupo:**
```bash
curl -X POST http://localhost:8080/api/grupos/criar/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "República da Ana",
    "descricao": "Grupo para dividir contas da república",
    "membrosIniciais": [2]
  }'
```

3. **Criar conta associada ao grupo:**
```bash
curl -X POST http://localhost:8080/api/contas \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "Conta de Luz",
    "valor": 150.00,
    "vencimento": "2025-08-15",
    "criadorId": 1,
    "grupoId": 1
  }'
```

4. **Listar contas do grupo:**
```bash
curl -X GET http://localhost:8080/api/contas/grupo/1
```

### Cenário Completo: Divisão por Porcentagem

1. **Criar conta para dividir:**
```bash
curl -X POST http://localhost:8080/api/contas \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "Jantar no restaurante",
    "valor": 100.00,
    "vencimento": "2025-08-15",
    "criadorId": 1
  }'
```

2. **Dividir por porcentagem:**
```bash
curl -X POST http://localhost:8080/api/divisoes/dividir-porcentagem \
  -H "Content-Type: application/json" \
  -d '{
    "contaId": 1,
    "divisoes": [
      {"usuarioId": 1, "percentual": 0.5},
      {"usuarioId": 2, "percentual": 0.3},
      {"usuarioId": 3, "percentual": 0.2}
    ]
  }'
```

3. **Verificar divisões criadas:**
```bash
curl -X GET http://localhost:8080/api/divisoes/conta/1
```

**Resultado:**
- Ana: R$ 50,00 (50% de R$ 100,00)
- Bruno: R$ 30,00 (30% de R$ 100,00)
- Carlos: R$ 20,00 (20% de R$ 100,00)

### Cenário Completo: Compra em Grupo

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