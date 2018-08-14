# Projeto da faculdade Infnet utilizando Android para avaliar as competências dos alunos.

#### Descrição do Sistema.  

Crie um projeto no Android Studio que deverá conter:
Tela Inicial: Tela inicial com uma logo, e três botões:
Login ( FIREBASE )  
Cadastro (FIREBASE )  
Entrar via Facebook ( FIREBASE + FACEBOOK )  
Tela de Cadastro: 
Formulário de cadastro com os seguintes campos:  
Nome  
Login (Email)  
Senha  
Confirmar Senha  
CPF  
Após o cadastro, o usuário deverá ser redirecionado para a Tela de Login para efetuar o Login.  
Tela de Login: Formulário de Login com os seguintes campos:  
Login (Email)  
Senha  
Tela Principal: Você deve apresentar uma listagem de tarefas para o usuário executar. Para buscar essas tarefas você deverá consumir o seguinte endpoint:
http://infnet.educacao.ws/dadosAtividades.php  
Consuma apenas os dados do campo “descricao”
Os dados devem ser exibidos em formato de ListView.
Para realizar a conexão com o WebService você deverá utilizar o RetroFit
