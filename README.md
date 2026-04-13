# Hupsteste

## Descrição

Hupsteste é uma aplicação Java desenvolvida com Spring Boot para gerenciar pacientes e suas avaliações. O sistema permite cadastrar pacientes com informações como nome, peso, altura e dias pós-RLCA, além de realizar avaliações relacionadas.

## Funcionalidades

- **Gerenciamento de Pacientes**: Cadastro, consulta e gerenciamento de dados de pacientes.
- **Avaliações**: Criação e gerenciamento de avaliações associadas aos pacientes.
- **Relatórios**: Geração de relatórios de avaliações usando templates HTML.

## Tecnologias Utilizadas

- **Java**: Linguagem de programação principal.
- **Spring Boot**: Framework para desenvolvimento de aplicações Java.
- **Spring Data JPA**: Para persistência de dados com Hibernate.
- **Spring Web**: Para criação de APIs REST.
- **Thymeleaf**: Para templates de relatórios.
- **Maven**: Gerenciamento de dependências e build.
- **H2 Database** (ou outro configurado): Banco de dados para desenvolvimento.

## Estrutura do Projeto

```
src/
├── main/
│   ├── java/
│   │   └── hup/teste/pacientes/hupsteste/
│   │       ├── HupstesteApplication.java          # Classe principal da aplicação
│   │       ├── business/
│   │       │   ├── avaliacoes/                    # Módulo de avaliações
│   │       │   │   ├── Avaliacao.java
│   │       │   │   ├── AvaliacaoController.java
│   │       │   │   ├── AvaliacaoRepository.java
│   │       │   │   └── AvaliacaoService.java
│   │       │   └── pacientes/                     # Módulo de pacientes
│   │       │       ├── Paciente.java
│   │       │       ├── PacienteController.java
│   │       │       ├── PacienteRepository.java
│   │       │       └── PacienteService.java
│   │       └── core/
│   │           ├── configs/                        # Configurações
│   │           │   └── WebConfig.java
│   │           ├── domains/                        # Domínios base
│   │           │   └── BaseModel.java
│   │           ├── exceptions/                     # Tratamento de exceções
│   │           │   ├── ErrorDetails.java
│   │           │   └── GlobalExceptionHandler.java
│   │           └── services/                       # Serviços utilitários
│   │               └── DateUtils.java
│   └── resources/
│       ├── application.properties                 # Configurações da aplicação
│       ├── static/                                # Recursos estáticos
│       └── templates/
│           └── relatorio-avaliacao.html           # Template para relatórios
└── test/
    └── java/
        └── hup/teste/pacientes/hupsteste/
            └── HupstesteApplicationTests.java     # Testes da aplicação
```

## Pré-requisitos

- **Java 17** ou superior instalado.
- **Maven** instalado (ou use o wrapper incluído).

## Como Executar

1. Clone o repositório:
   ```
   git clone <url-do-repositorio>
   cd hupsteste
   ```

2. Execute a aplicação usando o Maven wrapper:
   ```
   ./mvnw spring-boot:run
   ```

   Ou, se preferir usar Maven diretamente:
   ```
   mvn spring-boot:run
   ```

3. A aplicação estará rodando em `http://localhost:8080`.

## Configuração

As configurações da aplicação estão no arquivo `src/main/resources/application.properties`. Você pode ajustar propriedades como porta do servidor, configurações de banco de dados, etc.

## Testes

Para executar os testes:
```
./mvnw test
```

## Contribuição

1. Faça um fork do projeto.
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`).
3. Commit suas mudanças (`git commit -am 'Adiciona nova feature'`).
4. Push para a branch (`git push origin feature/nova-feature`).
5. Abra um Pull Request.

## Licença

Este projeto está sob a licença [MIT](LICENSE). Consulte o arquivo LICENSE para mais detalhes.

## Contato

Para dúvidas ou sugestões, entre em contato com [ramonsillva70@gmail.com] .
