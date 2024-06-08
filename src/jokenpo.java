public class jokenpo {
    public static String determinarVencedor(int escolhaJogador1, int escolhaJogador2) {
        if (escolhaJogador1 == escolhaJogador2) {
            return "Empate";
        } else if ((escolhaJogador1 == 1 && escolhaJogador2 == 3)
                || (escolhaJogador1 == 2 && escolhaJogador2 == 1)
                || (escolhaJogador1 == 3 && escolhaJogador2 == 2)) {
            return "Jogador 1 vence";
        } else {
            return "Jogador 2 vence";
        }
    }

    public static String converterEscolha(int escolha) {
        switch (escolha) {
            case 1:
                return "Pedra";
            case 2:
                return "Papel";
            case 3:
                return "Tesoura";
            default:
                return "";
        }
    }
}
