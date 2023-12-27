
# Enem Gamification - Instrucoes Tecnicas

## Tecnologias usadas

- Java 17
- Kotlin como Linguagem de programacao
- Spring 3.2
- MongoDB
- Test Containers para teste integrado
- Mockito para teste unitario
- REST API para integracao sincrona com a aplicacao

## Instrucoes para executar a aplicacao

Para executar a Aplicacao com um Banco MongoDB subindo em Docker basta executar o comando abaixo:

```shell
cd local/
docker-compose -f docker-compose-app.yml up
```

Para executar a aplicacao atraves do codigo-fonte:
```shell
# obs: requisitos sao java-17 e docker
./gradlew bootRun
```

## Arquitetura do Codigo (ou Layer Architecture)

```
|--- enem-gamification
           |--- app
                 |--- entrypoint
                           |--- entrypoint
                                  |--- QuestionRestController.kt
                            |--- handler
                                  |--- GlobalExceptionHandler.kt
           |--- domain
                   |--- dataproviders
                             |--- repository
                                      |--- QuestionRepository.kt
                             |--- httpclient
                   |--- entity
                           |--- QuestionEntity.kt
                   |--- usecase
                           |--- SaveAnswerUsecase.kt
```   

Inicialmente a aplicacao e dividido por dois modulos `app` e `domain`.    
- `App` possui tudo o que teremos de tecnologia. frameworks e entrada de dados da infraestrutura 
 para nossa aplicacao, como expor rotas HTTP para entrada de dados REST API.   
- `Domain` e' o core da nossa aplicacao, onde estara a regra de negocio, inicialmente 
 comecei a criar a aplicacao nao conhecendo tecnologias, framework ou infraestrutura utilizdasa,
 como MongoDB ou Spring, seguindo o conceito do livro `Clean Arch` escrito pelo Uncle Bob. No entanto
 optei por nao seguir o clean-arch by-the-book, e sim colocando em pratica um conceito 
 mais pratico e que facilita a leitura do codigo, mas o principalmente mesmo e' o domain
 conhecer a infraestrutura, ja que o maior ofensor de uma aplicacao e' a infra. Infrastrutura deve 
 fazer parte da regra de negocio, visando performance ou evitando gargalos.
- `Usecase` sao nossas classes com a regra de negocio de fato, seguindo o principio da responsabilidade unica.
- `DataProvider` e' a saida para nossa infraestrutura, como conecao ou operacao como o MongoDB. Se nossa aplicacao 
  conter outras saidas como infraestrutura como um HTTP Client ou um producer do Kafka, eles devem
  ficar tambem em dataproviders
- `Entity` onde estarao nossas classes que representa uma Entidade ou Documento no Banco de Dados.
    
Aqui tem outro exemplo no meu Github onde estudei em considerei usar um [Mix de Hexagonal + Clean Arch](SaveAnswerUsecase)

## Piramide de Testes

Um dos pontos mais importantes de desenvolver uma aplicacao e' a forma como voce a cria.
Seguindo o conceito da `Piramide de Teste` onde a Base sao os testes unitarios, a segunda camada da piramide
sao os testes integrados, e a ponta ou ultima parte da piramide sao os testes E2E. 
   
Nessa aplicacao usei o conceito TDD, iniciando pelos testes integrados onde eu desenvolvo um Endpoint 
comecando pelos testes. Como HttpClient para realizar os testes usei o RestTemplate, passando por todas as camadas 
da aplicacao, inclusive a saida para o Banco de Dados.   
    
A aplicaco tem duas divisoes, app e domain. App onde organizo e desenvolvo os testes integrados olhando para esse modulo.
Domain onde executo os temos unitarios, onde estao nossa regra de negocio.
    
#### Test Container

A saida para o Banco de Dados utilizei o Test-Container, dockers da nossa infra sao executados e apos os testes sao derrubados.
Nesse exemplo temos o MongoDB, antes utilizada o MongoDB Embbeded como teste para realizar esse tipo de teste, 
com a infra se tornando cada vez mais importante para evitar problemas em producao, testes de containers sao mais 
proximos de um cenario em producao, se sua aplicacao utiliza outras infraestruturas como Redis, Kafka, Oracle, HttpClient, 
Test-Container 'e uma boa solucao para realizar testes integrados com a Infra.

### Testes Unitarios

Os Testes unitario foram desenvolvidos em Kotlin e Mockito, Basicamente eles testam os `usecases`.