
# Enem Gamification - Instruções Técnicas

## Tecnologias usadas

- Java 17
- Kotlin como Linguagem de Programação
- Spring 3.2
- MongoDB
- Test Containers para teste integrado
- Mockito para teste unitario
- REST API para integração síncrona com a aplicação

## Arquitetura do Código (ou Layer Architecture)

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


Inicialmente, a aplicação é dividida em dois módulos: `app` e `domain`.
- `App` possui tudo o que teremos de tecnologia, frameworks e entrada de dados 
da infraestrutura para nossa aplicação, como expor rotas HTTP para entrada de dados REST API.   
- `Domain` é o core da nossa aplicação, onde estará a regra de negócio. 
Inicialmente, comecei a criar a aplicação sem conhecer as tecnologias, 
frameworks ou infraestrutura utilizados, como MongoDB ou Spring, seguindo o 
conceito do livro `Clean Architecture` escrito pelo Uncle Bob. No entanto, 
optei por não seguir a clean-arch "by-the-book" e sim colocar em prática um 
conceito mais prático que facilita a leitura do código. O principal é que o 
domain conheça a infraestrutura, já que o maior ofensor de uma aplicação é a 
infraestrutura. A infraestrutura deve fazer parte da regra de negócio, 
visando performance ou evitando gargalos.
- `Usecase` são nossas classes com a regra de negócio de fato, seguindo o princípio da responsabilidade única.
- `DataProvider`  é a saída para nossa infraestrutura, como conexão ou operação com o MongoDB. Se nossa aplicação conter outras saídas como infraestrutura como um HTTP Client ou um producer do Kafka, eles devem ficar também em dataproviders. 
  conter outras saidas como infraestrutura como um HTTP Client ou um producer do Kafka, eles devem
  ficar tambem em dataproviders
- `Entity` é onde estarão nossas classes que representam uma Entidade ou Documento no Banco de Dados.
    
Aqui tem outro exemplo no meu Github onde estudei em considerei usar um [Mix de Hexagonal + Clean Arch](SaveAnswerUsecase)

## Pirâmide de Testes

Um dos pontos mais importantes de desenvolver uma aplicação é a forma como você a cria. Seguindo o conceito da Pirâmide de Teste, onde a base são os testes unitários, a segunda camada da pirâmide são os testes integrados, e a ponta ou última parte da pirâmide são os testes E2E.

Nessa aplicação, usei o conceito TDD, iniciando pelos testes integrados, onde eu desenvolvo um Endpoint começando pelos testes. Como HttpClient para realizar os testes, usei o RestTemplate, passando por todas as camadas da aplicação, inclusive a saída para o Banco de Dados. 

A aplicação tem duas divisões, app e domain. App onde organizo e desenvolvo os testes integrados olhando para esse módulo. Domain onde executo os testes unitários, onde está nossa regra de negócio.
    
### Test Container

A saída para o Banco de Dados utilizei o Test-Container, dockers da nossa infra são executados e após os testes são derrubados. Nesse exemplo, temos o MongoDB. Antes, utilizei o MongoDB Embedded como teste para realizar esse tipo de teste. Com a infra se tornando cada vez mais importante para evitar problemas em produção, testes de containers são mais próximos de um cenário em produção. Se sua aplicação utiliza outras infraestruturas como Redis, Kafka, Oracle, HttpClient, Test-Container é uma boa solução para realizar testes integrados com a Infra.

### Testes Unitários

Os Testes unitários foram desenvolvidos em Kotlin e Mockito. Basicamente, eles testam os `usecases`.
    
## Endpoints 

Apos rodar a aplicacao para ver os endpoints acesse: http://localhost:8080/swagger-ui.html
    
Get Questions Paged
```shell
curl -X GET -H "testId: 658cd497baf2f726dc40eacc" http://localhost:8080/v1/questions
```

## Instruções para Executar a Aplicação

Para executar a aplicação com um Banco MongoDB subindo em Docker, basta executar o comando abaixo:
Dois containers ficarao UP, mongo e a aplicacao ([imagem da aplicacao esta no dockerhub e ira fazer o pull do mesmo](https://hub.docker.com/repository/docker/diegolirio/enem-gamification/general))
```shell
cd local/
docker-compose -f docker-compose-app.yml up
```

Para executar a aplicação através do código-fonte:
```shell
# obs: requisitos sao java-17 e docker
./gradlew bootRun
```

## Gerar Imagem Docker

```shell
sh build-and-generate-image.sh
```
