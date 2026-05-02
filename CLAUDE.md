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

- **pacientes/** — CRUD de pacientes + `RelatorioService` (relatório evolutivo em PDF) — requer autenticação
- **avaliacoes/** — CRUD de avaliações + PDF individual de avaliação — requer autenticação
- **usuarios/** — Autenticação e registro de usuários (`/api/v1/auth`) — público

Cada módulo segue: `Entidade`, `Controller`, `Service`, `Repository`, `dto/`.

Utilitários centrais em `core/`:
- `domains/BaseModel.java` — superclasse JPA abstrata com UUID (`GenerationType.UUID`) e `dataHoraCriacao` (`@PrePersist`); todas as entidades a estendem
- `configs/SecurityConfig.java` — Spring Security (JWT stateless, regras de rotas, CORS inline)
- `security/JwtService.java` — geração e validação de JWT
- `security/JwtAuthFilter.java` — filtro JWT por requisição (`OncePerRequestFilter`)
- `exceptions/GlobalExceptionHandler.java` — `ResourceNotFoundException` → 404, `MethodArgumentNotValidException` → 400, `AuthenticationException` → 401, `Exception` → 500
- `services/DateUtils.java` — utilitário de datas

> **Nota:** não existe `WebConfig.java` separado — o CORS é configurado diretamente em `SecurityConfig.java`.

## Endpoints

### Autenticação (público — sem token)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/v1/auth/registrar` | Registrar novo usuário → `{ token, email, nome }` |
| POST | `/api/v1/auth/login` | Login → `{ token, email, nome }` |

### Pacientes (requer `Authorization: Bearer <token>`)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/v1/pacientes` | Criar paciente |
| GET | `/api/v1/pacientes` | Listar todos os pacientes |
| GET | `/api/v1/pacientes/{id}` | Buscar paciente por UUID |
| PUT | `/api/v1/pacientes/{id}` | Atualizar paciente |
| DELETE | `/api/v1/pacientes/{id}` | Deletar paciente |
| GET | `/api/v1/pacientes/{id}/relatorio-pdf` | Relatório evolutivo de todas avaliações do paciente (PDF) |

### Avaliações (requer `Authorization: Bearer <token>`)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/v1/avaliacoes` | Criar avaliação |
| GET | `/api/v1/avaliacoes` | Listar todas as avaliações |
| GET | `/api/v1/avaliacoes/{id}` | Buscar avaliação por UUID |
| PUT | `/api/v1/avaliacoes/{id}` | Atualizar avaliação |
| DELETE | `/api/v1/avaliacoes/{id}` | Deletar avaliação |
| GET | `/api/v1/avaliacoes/{id}/pdf` | PDF individual da avaliação (Thymeleaf + Flying Saucer) |
| GET | `/api/v1/avaliacoes/paciente/{pacienteId}` | Listar avaliações de um paciente |
| GET | `/api/v1/avaliacoes/paciente/{pacienteId}/relatorio` | Relatório evolutivo do paciente (mesmo PDF que `/pacientes/{id}/relatorio-pdf`) |

## Segurança

O Spring Security está configurado como stateless. Toda requisição para rotas fora de `/api/v1/auth/**` deve incluir um JWT válido no header `Authorization: Bearer <token>`.

Fluxo: `JwtAuthFilter` extrai o token → `JwtService` valida assinatura e expiração → carrega `UserDetails` do `UsuarioRepository` → define autenticação no `SecurityContextHolder`.

`Usuario` implementa `UserDetails`. Papéis: `USER`, `ADMIN` (armazenados como `ROLE_USER` / `ROLE_ADMIN`).

## Geração de PDF

Há dois fluxos distintos de geração de PDF:

**1. PDF individual da avaliação** — `AvaliacaoService.gerarPdf(UUID id)`
Calcula o LSI para cada teste: `(min(D, E) / max(D, E)) * 100`. Aptidão: LSI ≥ 90%. Busca histórico via `findTop5ByPacienteIdOrderByDataHoraCriacaoAsc()` para montar gráfico externo via QuickChart (URL gerada no template). Renderiza com Thymeleaf + Flying Saucer (OpenPDF / `flying-saucer-pdf-openpdf`). Template: `src/main/resources/templates/relatorio-avaliacao.html`.

**2. Relatório evolutivo do paciente** — `RelatorioService.gerarRelatorioEvolutivo(UUID pacienteId)`
Busca todas as avaliações do paciente (`findByPacienteIdOrderByDataHoraCriacaoAsc()`). Gera gráfico de linha com JFreeChart (biblioteca Java, sem HTTP externo). Monta o PDF programaticamente com iText 5 (`com.itextpdf:itextpdf:5.5.13.4`). Exposto tanto em `GET /api/v1/pacientes/{id}/relatorio-pdf` quanto em `GET /api/v1/avaliacoes/paciente/{pacienteId}/relatorio`.

## Modelo de Dados

Todos os DTOs são Java `record`. O campo `id` nunca é enviado na criação.

### Paciente
| Campo | Tipo | Observações |
|-------|------|-------------|
| id | UUID | gerado automaticamente |
| nome | String | `@NotBlank` |
| peso | String | `@NotBlank` |
| altura | String | `@NotBlank` |
| dataCirugia | LocalDate | `@NotNull`; coluna DB: `data_cirurgia`; JSON: `yyyy-MM-dd` |
| membro_operado | String | `@NotBlank`; coluna DB: `membro_op` |
| diasPosOperatorio | Long | somente leitura; `@Transient` getter na entidade; calculado via `ChronoUnit.DAYS.between(dataCirugia, now())` |

> A entidade `Paciente` também persiste `diasPosRlca` (coluna `dias_pos_rlca`) no banco via `@PrePersist`/`@PreUpdate` — mesmo valor que `diasPosOperatorio`, mas armazenado para consultas SQL diretas.

### Avaliacao
| Campo | Tipo | Observações |
|-------|------|-------------|
| id | UUID | gerado automaticamente |
| pacienteId | UUID | `@NotNull`; ManyToOne na entidade |
| dataAvaliacao | String | somente leitura; derivado de `dataHoraCriacao` (`yyyy-MM-dd`) |
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
SPRING_DATASOURCE_URL       # URL de conexão com o PostgreSQL (inclui credenciais do pooler Supabase)
SPRING_DATASOURCE_PASSWORD  # Senha do banco de dados
JWT_SECRET                  # Chave HMAC codificada em Base64 (mínimo 256 bits)
JWT_EXPIRATION              # Tempo de vida do token em ms (padrão: 86400000 = 24h)
PORT                        # Porta do servidor (padrão: 8080)
```

> `SPRING_DATASOURCE_USERNAME` não está mapeado em `application.properties` — se necessário, deve ser incluído na `SPRING_DATASOURCE_URL`.

`spring.jpa.hibernate.ddl-auto=update` — schema é atualizado na inicialização, dados existentes são preservados. HikariCP configurado com pool máximo de 5 conexões e `connection-test-query=SELECT 1` (compatibilidade com PgBouncer).

## Regras do Projeto
- Idioma padrão: Português Brasileiro.
- Todos os arquivos `.md` devem ser gerados em PT-BR.
- Ao trabalhar em qualquer tarefa de front-end, sempre consultar este arquivo (`CLAUDE.md`) para garantir que endpoints, autenticação (JWT), payloads e modelos de dados estejam alinhados com o back-end.
