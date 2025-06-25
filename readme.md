# 💰 Sistema de Gerenciamento Financeiro

## 📋 Visão Geral

O **Sistema de Gerenciamento Financeiro** é uma aplicação web desenvolvida para auxiliar usuários no controle de suas finanças pessoais. O sistema permite o cadastro e categorização de transações financeiras, geração de relatórios e gráficos, além de notificações automáticas para vencimentos.

### 🎯 Propósito

- **Controle Financeiro Pessoal**: Registro e acompanhamento de receitas e despesas
- **Categorização Inteligente**: Organização das transações por categorias personalizáveis
- **Análise Visual**: Dashboards e gráficos para análise de padrões financeiros
- **Gestão de Vencimentos**: Sistema de notificações para contas a pagar

---

## 🛠️ Tecnologias Utilizadas

### Backend
- **Java 21** - Linguagem principal
- **Spring Boot 3.3.4** - Framework principal
- **Spring Security** - Autenticação e autorização
- **Spring Data JPA** - Persistência de dados
- **H2 Database** - Banco de dados em memória (desenvolvimento)
- **JWT** - Autenticação stateless
- **MapStruct** - Mapeamento entre camadas
- **RabbitMQ** - Mensageria para notificações

### Infraestrutura e DevOps
- **Docker & Docker Compose** - Containerização
- **GitHub Actions** - CI/CD
- **Maven** - Gerenciamento de dependências
- **Swagger/OpenAPI** - Documentação da API

### Testes
- **JUnit 5** - Framework de testes
- **Mockito** - Mocks para testes unitários
- **TestContainers** - Testes de integração

### Arquitetura
- **Hexagonal Architecture** - Separação de responsabilidades
- **Domain-Driven Design (DDD)** - Modelagem do domínio
- **Ports & Adapters** - Isolamento de dependências externas

---

## ✅ Funcionalidades Implementadas

### 🔐 Autenticação e Autorização
- [x] **Cadastro de usuários** com validação de email único
- [x] **Login com JWT** - Tokens seguros para autenticação
- [x] **Controle de acesso** - Usuários só acessam seus próprios dados
- [x] **Criptografia de senhas** com BCrypt

### 📊 Gestão de Categorias
- [x] **Criação de categorias personalizadas** por usuário
- [x] **Categorias padrão** criadas automaticamente no cadastro
- [x] **Busca paginada** de categorias
- [x] **Validação de propriedade** - Usuários só acessam suas categorias

### 💳 Gestão de Transações
- [x] **Criação de transações** (receitas e despesas)
- [x] **Associação com categorias** definidas pelo usuário
- [x] **Busca por categoria** e filtros por usuário
- [x] **Cálculo automático de saldo** baseado nas transações
- [x] **Atualização de categoria** de transações existentes

### 📈 Relatórios e Gráficos
- [x] **Gráfico por categoria** - Visualização de gastos por categoria
- [x] **Evolução financeira mensal** - Receitas vs despesas ao longo do tempo
- [x] **Resumo financeiro** - Totalizadores de receitas, despesas e saldo

### 🔔 Sistema de Notificações
- [x] **Notificações de vencimento** via RabbitMQ
- [x] **Processamento assíncrono** com delayed messages
- [x] **Consumer de mensagens** para processamento de notificações

### 📚 Documentação e Qualidade
- [x] **Documentação da API** com Swagger/OpenAPI
- [x] **Testes unitários e de integração** com boa cobertura
- [x] **Tratamento global de exceções** com respostas padronizadas
- [x] **Validação de entrada** em todos os endpoints

---

## ❌ Funcionalidades Pendentes

### 🏗️ **Melhorias Arquiteturais (Alta Prioridade)**

#### **Pureza da Arquitetura Hexagonal**
**Status:** 🔴 Pendente  
**Motivo:** Priorização por velocidade de entrega inicial

- [ ] **Separação completa do domínio**: Remover anotações JPA das entidades de domínio
- [ ] **Modularização em Maven/Gradle**: Criar módulos separados (domain, application, adapters)
- [ ] **Use Cases específicos**: Quebrar Services em Use Cases únicos e focados
- [ ] **Domain Events**: Implementar eventos de domínio para comunicação assíncrona

**Justificativa:** No MVP foi priorizada a entrega rápida. Agora é necessário refatorar para atingir pureza arquitetural e facilitar manutenibilidade a longo prazo.

#### **Implementação de CQRS**
**Status:** 🟡 Planejado  
**Motivo:** Decisão arquitetural para melhor performance em consultas

- [ ] **Command side**: Separar operações de escrita
- [ ] **Query side**: Criar projeções otimizadas para leitura
- [ ] **Event Sourcing**: Considerar para auditoria completa

### 💾 **Persistência e Performance**

#### **Banco de Dados de Produção**
**Status:** 🔴 Crítico  
**Motivo:** Atualmente usando H2 apenas para desenvolvimento

- [ ] **PostgreSQL**: Configurar para produção
- [ ] **Migrations com Flyway**: Versionamento do schema
- [ ] **Connection pooling**: Otimização de conexões
- [ ] **Índices estratégicos**: Performance em consultas frequentes

#### **Cache e Otimizações**
**Status:** 🟡 Futuro  
**Motivo:** Prematuridade - aguardando métricas de uso real

- [ ] **Redis**: Cache para consultas frequentes
- [ ] **Cache de segundo nível**: Hibernate L2 cache
- [ ] **Paginação otimizada**: Cursor-based pagination para grandes volumes

### 🔒 **Segurança Avançada**

#### **Melhorias de Segurança**
**Status:** 🟡 Médio prazo  
**Motivo:** Funcionalidades básicas atendendo demanda atual

- [ ] **Rate limiting**: Proteção contra ataques de força bruta
- [ ] **Refresh tokens**: Renovação segura de JWT
- [ ] **2FA**: Autenticação de dois fatores
- [ ] **Auditoria completa**: Log de todas as operações sensíveis

### 📱 **Interface e Experiência**

#### **Frontend Web**

Status: 🟡 Em desenvolvimento
Repositório: [gerenciador-financas-app](https://github.com/ViniciusKoiti/gerenciador-financas-app)
Motivo: Desenvolvimento paralelo para validação da API

#### **API Mobile**
**Status:** 🟡 Futuro  
**Motivo:** Validação de mercado pendente

- [ ] **Endpoints otimizados**: Para consumo mobile
- [ ] **Push notifications**: Notificações nativas
- [ ] **Sincronização offline**: Para uso sem internet

### 🔄 **Integrações Externas**

#### **Importação de Dados**
**Status:** 🟡 Planejado  
**Motivo:** Alinhamento com padrões bancários brasileiros

- [ ] **OFX/QIF**: Importação de extratos bancários
- [ ] **Open Banking**: Integração com APIs bancárias brasileiras
- [ ] **CSV customizável**: Upload com mapeamento de campos
- [ ] **Categorização automática**: IA para sugerir categorias

#### **Relatórios Avançados**
**Status:** 🟡 Média prioridade  
**Motivo:** Funcionalidades básicas atendendo usuários iniciais

- [ ] **Exportação PDF**: Relatórios formatados
- [ ] **Excel/CSV**: Exportação de dados para análise externa
- [ ] **Projeções financeiras**: Simulações baseadas em histórico
- [ ] **Alertas inteligentes**: Notificações baseadas em padrões

### 🚀 **Performance e Escalabilidade**

#### **Observabilidade**
**Status:** 🟡 Próxima sprint  
**Motivo:** Necessário para monitoramento em produção

- [ ] **Métricas customizadas**: Micrometer + Prometheus
- [ ] **Tracing distribuído**: Jaeger/Zipkin
- [ ] **Logs estruturados**: JSON para análise automatizada
- [ ] **Health checks**: Monitoramento de dependências

#### **Testes Avançados**
**Status:** 🟡 Melhoria contínua  
**Motivo:** Cobertura atual atende, mas pode ser aprimorada

- [ ] **Testes de carga**: JMeter/Gatling
- [ ] **Testes de contrato**: Pact para APIs
- [ ] **Mutation testing**: Qualidade dos testes
- [ ] **Testes arquiteturais**: ArchUnit para validação automática

---

## 🚀 Próximos Passos

### **Sprint 1 - Pureza Arquitetural** (2 semanas)
1. Remover anotações JPA do domínio
2. Criar entidades JPA separadas nos adapters
3. Implementar mappers entre domínio e persistência

### **Sprint 2 - Modularização** (2 semanas)
1. Separar em módulos Maven
2. Quebrar Services em Use Cases específicos
3. Implementar testes arquiteturais com ArchUnit

### **Sprint 3 - Banco de Produção** (1 semana)
1. Configurar PostgreSQL
2. Ajustar Flyway migrations
3. Testes de integração com TestContainers

---

## 📊 Status do Projeto

| Categoria | Progresso | Status |
|-----------|-----------|--------|
| **MVP Funcional** | 95% | ✅ Concluído |
| **Arquitetura Hexagonal** | 75% | 🟡 Em evolução |
| **Testes e Qualidade** | 80% | ✅ Bom |
| **Documentação** | 85% | ✅ Bom |
| **Segurança Básica** | 90% | ✅ Concluído |
| **Performance** | 60% | 🟡 A melhorar |
| **Produção Ready** | 70% | 🟡 Quase pronto |

---

## 🤝 Como Contribuir

1. **Fork** o repositório
2. Crie uma **branch** para sua feature (`git checkout -b feature/nova-funcionalidade`)
3. **Commit** suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. **Push** para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um **Pull Request**

## 📞 Contato

- **Desenvolvedor**: Vinicius
- **Email**: viniciusnakahara@gmail.com
- **GitHub**: [ViniciusKoiti](https://github.com/ViniciusKoiti/)

---

