Para executar o sistema:

    1° - Em um terminal, executar o comando 'mosquitto'.

    2° - Em outro terminal, executar o comando './servers_start.sh'.
    Isto inicializará os servidores:
        AdminPortalServer;
        OrderPortalServer;
    Todos serão executados em paralelo.
    
    3° - Em outro terminal, executar o comando './admin_client_start.sh'.
    Isto inicializará o cliente AdminPortalCleint. É através desta janela
    que o usuário interagirá com o Portal de Administrador.
    
    4° - Em outro terminal, executar o comando './order_client_start.sh'.
    Isto inicializará o cliente OrderPortalClient. É através desta janela
    que o usuário interagirá com o Portal de Pedidos.

Para executar os testes automatizados:

    1° - Para quaisquer testes, é necessário executar o comando 'mosquitto'.

    2° - Para os testes especificamente relacionados ao Portal de Pedidos,
    é necessário executar o 'servers_start.sh'.

    3° - Para acessar os testes, referir-se aos arquivos contidos na pasta:
    './src/test/java'.