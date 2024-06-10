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
}
