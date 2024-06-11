package jogo;

import util.Mensagem;

public class Jokenpo {

    public String jogar(Mensagem player1, Mensagem player2) {
        String resultado;
        if (player1.getTexto().equals(player2.getTexto())) {
            resultado = "empate";
        } else if ((player1.getTexto().equals("pedra") && player2.getTexto().equals("tesoura"))
                || (player1.getTexto().equals("tesoura") && player2.getTexto().equals("papel"))
                || (player1.getTexto().equals("papel") && player2.getTexto().equals("pedra"))) {
            resultado = player1.getNome();
        } else {
            resultado = player2.getNome();
        }

        return resultado;
    }

    public String jogarContraCPU(Mensagem player) {
        String[] opcoes = {"pedra", "papel", "tesoura"};
        String escolhaCPU = opcoes[(int) (Math.random() * 3)];

        String resultado;
        if (player.getTexto().equals(escolhaCPU)) {
            resultado = "empate";
        } else if ((player.getTexto().equals("pedra") && escolhaCPU.equals("tesoura"))
                || (player.getTexto().equals("tesoura") && escolhaCPU.equals("papel"))
                || (player.getTexto().equals("papel") && escolhaCPU.equals("pedra"))) {
            resultado = player.getNome();
        } else {
            resultado = "CPU";
        }

        return resultado;
    }
}
