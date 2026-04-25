# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Visão Geral do Projeto

API REST para gerenciamento de pacientes e avaliações de testes funcionais de hop (reabilitação pós-cirurgia de RLCA), construída com Spring Boot 4.0.5 e Java 21. Os dados são armazenados em PostgreSQL (Supabase). A autenticação é feita via JWT (JJWT 0.12.6).

## Comandos

```bash
# Rodar a aplicação (http://localhost:8080)
./mvnw spring-boot:run

# Build
./mvnw clean install

# Rodar todos os testes
./mvnw test

# Rodar uma classe de teste específica
./mvnw test -Dtest=PacienteApiTest

# Rodar um método de teste específico
./mvnw test -Dtest=PacienteApiTest#testPacienteApiTestLoads
```

## Arquitetura

Arquitetura em 3 camadas: **Controller → Service → Repository → PostgreSQL**

Três módulos de negócio em `src/main/java/hup/teste/pacientes/hupsteste/business/`:

- **pacientes/** — CRUD de pacientes (`/api/v1/pacientes`) — requer autenticação
- **avaliacoes/** — CRUD de avaliações + geração de PDF (`/api/v1/avaliacoes`, `/api/v1/avaliacoes/{id}/pdf`) — requer autenticação
- **usuarios/** — Autenticação e registro de usuários (`/api/v1/auth`) — público

Cada módulo segue a mesma estrutura: `Entidade`, `Controller`, `Service`, `Repository`, `dto/`.

Utilitários centrais em `core/`:
- `domains/BaseModel.java` — superclasse JPA abstrata com UUID (`GenerationType.UUID`) e `dataHoraCriacao` (preenchido via `@PrePersist`); todas as entidades a estendem
- `configs/SecurityConfig.java` — configuração do Spring Security (JWT, sessões stateless, regras de rotas)
- `configs/WebConfig.java` — configuração de CORS
- `security/JwtService.java` — geração e validação de JWT
- `security/JwtAuthFilter.java` — filtro JWT por requisição (estende `OncePerRequestFilter`)
- `exceptions/GlobalExceptionHandler.java` — tratamento centralizado de erros (`ResourceNotFoundException` → 404, `MethodArgumentNotValidException` → 400, `AuthenticationException` → 401, `Exception` → 500)
- `services/DateUtils.java` — utilitário de datas

## Endpoints

### Autenticação (público — sem token)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/v1/auth/registrar` | Registrar novo usuário → retorna `{ token, email, nome }` |
| POST | `/api/v1/auth/login` | Login → retorna `{ token, email, nome }` |

### Pacientes (requer `Authorization: Bearer <token>`)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/v1/pacientes` | Criar paciente |
| GET | `/api/v1/pacientes` | Listar todos os pacientes |
| GET | `/api/v1/pacientes/{id}` | Buscar paciente por UUID |
| PUT | `/api/v1/pacientes/{id}` | Atualizar paciente |
| DELETE | `/api/v1/pacientes/{id}` | Deletar paciente |

### Avaliações (requer `Authorization: Bearer <token>`)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/v1/avaliacoes` | Criar avaliação |
| GET | `/api/v1/avaliacoes` | Listar todas as avaliações |
| GET | `/api/v1/avaliacoes/{id}` | Buscar avaliação por UUID |
| PUT | `/api/v1/avaliacoes/{id}` | Atualizar avaliação |
| DELETE | `/api/v1/avaliacoes/{id}` | Deletar avaliação |
| GET | `/api/v1/avaliacoes/{id}/pdf` | Baixar relatório em PDF |

## Segurança

O Spring Security está configurado como stateless (sem sessões/cookies). Toda requisição para rotas fora de `/api/v1/auth/**` deve incluir um JWT válido no header `Authorization: Bearer <token>`.

Fluxo: `JwtAuthFilter` extrai o token → `JwtService` valida assinatura e expiração → carrega `UserDetails` do `UsuarioRepository` → define autenticação no `SecurityContextHolder`.

`Usuario` implementa `UserDetails`. Papéis: `USER`, `ADMIN` (armazenados como `ROLE_USER` / `ROLE_ADMIN`).

## Geração de PDF

`AvaliacaoService.gerarPdf()` calcula o LSI (Índice de Simetria do Membro) para cada teste de hop — fórmula: `(min(D, E) / max(D, E)) * 100`. Limiar de aptidão: LSI ≥ 90%. Também busca as últimas 5 avaliações do paciente via `AvaliacaoRepository.findTop5ByPacienteIdOrderByDataHoraCriacaoAsc()` para renderizar um gráfico de evolução via QuickChart. O PDF é gerado com Thymeleaf + Flying Saucer (OpenPDF); o template está em `src/main/resources/templates/relatorio-avaliacao.html`.

## Modelo de Dados

Todos os DTOs são Java `record`. O campo `id` nunca é enviado na criação — é ignorado se presente.

### Paciente
| Campo | Tipo | Observações |
|-------|------|-------------|
| id | UUID | gerado automaticamente |
| nome | String | `@NotBlank` |
| peso | String | `@NotBlank` |
| altura | String | `@NotBlank` |
| dataCirugia | LocalDate | `@NotNull`; coluna DB: `data_cirurgia`; JSON: formato `yyyy-MM-dd` (`@JsonFormat`) |
| diasPosOperatorio | Long | `@Transient` — calculado via `ChronoUnit.DAYS.between(dataCirugia, LocalDate.now())` |

### Avaliacao
| Campo | Tipo | Observações |
|-------|------|-------------|
| id | UUID | gerado automaticamente |
| pacienteId | UUID | referência ao paciente (ManyToOne na entidade) |
| singleHopDireita/Esquerda | Double | `@NotNull @Positive` |
| tripleHopDireita/Esquerda | Double | `@NotNull @Positive` |
| crossoverHopDireita/Esquerda | Double | `@NotNull @Positive` |
| sixMeterDireita/Esquerda | Double | `@NotNull @Positive` |

### Usuario
| Campo | Tipo | Observações |
|-------|------|-------------|
| id | UUID | gerado automaticamente |
| nome | String | obrigatório |
| email | String | único, obrigatório |
| senha | String | hash BCrypt |
| role | Role (enum) | USER ou ADMIN |

## Variáveis de Ambiente

```
SPRING_DATASOURCE_URL       # URL de conexão com o PostgreSQL
SPRING_DATASOURCE_USERNAME  # Usuário do banco de dados
SPRING_DATASOURCE_PASSWORD  # Senha do banco de dados
JWT_SECRET                  # Chave HMAC codificada em Base64 (mínimo 256 bits)
JWT_EXPIRATION              # Tempo de vida do token em milissegundos (padrão: 86400000 = 24h)
```

`spring.jpa.hibernate.ddl-auto=update` — o schema é atualizado na inicialização, os dados existentes são preservados.

## Regras do Projeto
- Idioma padrão: Português Brasileiro.
- Todos os arquivos `.md` devem ser gerados em PT-BR.
- Ao trabalhar em qualquer tarefa de front-end, sempre consultar este arquivo (`CLAUDE.md`) para garantir que endpoints, autenticação (JWT), payloads e modelos de dados estejam alinhados com o back-end.
