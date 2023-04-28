[NOSSO VÍDEO](https://youtu.be/49kOjDBGJcU)

# Para executar o sistema:

    1° - Em um terminal, executar o comando 'mosquitto'.

    2° - Em outro terminal, executar o comando './servers_start.sh'.
    Isto inicializará (2 de cada) os servidores:
        AdminPortalServer;
        OrderPortalServer;
    Todos serão executados em paralelo.
    
    3° - Em outro terminal, executar o comando './admin_client_start.sh'.
    Isto inicializará o cliente AdminPortalCleint. É através desta janela
    que o usuário interagirá com o Portal de Administrador.
    
    4° - Em outro terminal, executar o comando './order_client_start.sh'.
    Isto inicializará o cliente OrderPortalClient. É através desta janela
    que o usuário interagirá com o Portal de Pedidos.

    5° - Ao final da execução do comando './servers_start.sh', é possível
    executar o comando './matar_portas_servers.sh', para garantir o bom
    funcionamento do sistema entre execuções. E evitar com que fique 
    aberto os sistemas dependendo de como encerrar o programa...
    
## Compilação

`servers_start.sh`, ` admin_client_start.sh `... compõe nossos scripts. São autoexplicativos, inicializam ou o server
ou os servers, ou um client etc. Exceto o servers_start, aceitam porta como entrada.

Olhe dentro de cada script para entender como executar quaisquer classe, mas em geral seria (no mínimo)

`./gradlew extractIncludeProto extractProto generateProto compileJava processResources classes run -PmainClass=ufu.davigabriel.Caminho.Pra.Classe --args="Porta?"`

Se não passar a porta, as nossas base serão usadas. Recomenda-se fortemante mantê-las.

# Para executar os testes automatizados:

    1° - Para quaisquer testes, é necessário executar o comando 'mosquitto'.

    2° - Para os testes especificamente relacionados ao Portal de Pedidos,
    é necessário executar o 'servers_start.sh'.

    3° - Para acessar os testes, referir-se aos arquivos contidos na pasta:
    './src/test/java'. Recomendamos utilizar o Intellij.

É possível utilizar os comandos incluídos no

# VERSÕES

JAVA 17
GRADLE 8.0
Bibliotecas e dependências no build.gradle

# O que foi coberto

- CRUD Client, Product e Pedidos
- "Login"
- Múltiplos acessos
- Múltiplos Servers
- GRPC para comunicação com servers
- Pub/Sub para publicar mudanças nos tópicos do mosquitto e escutar via callback

# O que poderia mudar, com outros requisitos

- Melhor tratamento de erros do Mosquitto
- Substituir processos faltosos
- Criar um balanceamento de carga/mecanismo automático de escolher client
- Revisar ter que receber inclusive a própria mensagem.
- Multithreading?
- Melhorias no processamento (como foi dito, meu computador sequer conseguia gravar, compartilhar tela, compilar e rodar 4 servers + 4 clients + testes...
