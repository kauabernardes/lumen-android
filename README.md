# 📱 Lumen - Plataforma de Gestão de Estudos e Comunidade

O **Lumen** é um aplicativo mobile desenvolvido com o intuito de auxiliar os alunos através da organização, acompanhamento e simplificação do acesso aos estudos. Este repositório contém o código fonte Android e a documentação exigida para o **Projeto Integrador Completo** da disciplina de Dispositivos Móveis. 

---

## 🎯 Objetivo do Projeto
Atendendo aos requisitos acadêmicos, o projeto evoluiu do protótipo inicial para um aplicativo funcional nativo contendo o modelo de dados completo e as interfaces de usuário operacionais. 

### 📋 Requisitos Atendidos
* **Múltiplas Telas:** O aplicativo possui mais de 7 telas implementadas no Android (Ex: Autenticação, Home, Perfil de Usuário, Explorar Comunidades, Feed, Agenda e Check-in Diário).
* **Navegação Estruturada:** Implementação de menu com no mínimo 6 itens de navegação.
* **Autenticação de Usuário:** Sistema de login integrado para autenticação do usuário.
* **Integração com Banco de Dados:** Funcionalidades de inclusão e consulta integradas ao banco de dados e API.

---

## ⚙️ Arquitetura e Comunicação (Como Funciona)

O Lumen foi projetado seguindo as melhores práticas do desenvolvimento Android moderno, utilizando uma arquitetura cliente-servidor robusta.

### Cliente (Android Nativo)
* **Linguagem & UI:** Desenvolvido 100% em **Kotlin**, utilizando as bibliotecas do Material Design 3 e layouts em XML.
* **Assincronicidade:** Uso intensivo de **Coroutines** (`lifecycleScope.launch`) para garantir que a interface não congele durante o processamento de dados.
* **Navegação:** Utilização do **Navigation Component** para transições fluidas e passagem de argumentos seguros entre fragments.

### Comunicação com a API (Backend Node.js / NestJS)
Toda a persistência e lógica de negócios real ocorre em uma API centralizada.
* **Retrofit & OkHttp:** A comunicação HTTP é realizada utilizando a biblioteca `RetrofitClient`.
* **SocketIO:** A transmissão em tempo real de sessões de estudos é implementada por websockets com a biblioteca `SocketIO`.
* **Autenticação (JWT):** O aplicativo utiliza um gerenciador local (`TokenManager`) para armazenar e enviar o Bearer Token.

---

## 🎨 UI/UX e Padrões de Código

A interface foi cuidadosamente projetada para ser amigável e acessível, respeitando as exigências visuais:
* **Gestalt e Acessibilidade:** Interface amigável com identidade visual adequada, navegabilidade e botões grandes (mínimo 44px), respeitando princípios de Gestalt e a lei de Fitts[cite: 1].
* **Boas Práticas Kotlin:** O código evita funções muito longas, utiliza responsabilidade única, prefere `val` em vez de `var` e evita o operador `!!` (not-null assertion) sempre que possível. Uso de pelo menos 1 função de extensão e 1 RecyclerView. Todas as strings literais foram extraídas para o arquivo `strings.xml`.
* **Clean Code:** Código fonte indentado e comentado apenas no que não for óbvio[cite: 1].

---

## 📂 Estrutura de Documentação (`/docs`)

Conforme os critérios de avaliação do projeto, toda a documentação teórica e de modelagem encontra-se na pasta **`docs/`** na raiz deste repositório, incluindo de forma organizada:

1.  📄 **`Modelo de Entidade e Relacionamento.pdf`**: Diagrama contendo as entidades, relacionamentos, atributos e cardinalidades (1:1, 1:N, N:M).
2.  💾 **`SQL.sql`**: Código SQL completo para geração do modelo de dados.
3.  📊 **`Apresentacao.pdf`**: Slides da apresentação oficial contendo a capa, justificativa, objetivos, funcionalidades e prints detalhados das Activitys/Fragments desenvolvidas.

---

## 🚀 Como Executar o Projeto

1.  Clone este repositório:
    `git clone https://github.com/seu-usuario/lumen-android.git`
2.  Abra o projeto no **Android Studio**.
3.  Aguarde o **Gradle Sync** baixar as dependências (`Retrofit`, `Glide`, `Navigation Component`, etc.).
4.  Execute o aplicativo em um Emulador ou Dispositivo Físico Android.