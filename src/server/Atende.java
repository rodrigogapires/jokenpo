package server;

import java.net.Socket;
import util.Comunicacao;
import util.Mensagem;
import jogo.Jokenpo;

public class Atende extends Thread {
    private Socket clienteSocket;
    private Comunicacao comunicacao;
    private String jogadorNome;
    private String jogadorJogada;
    private Atende oponente;
    private static Jokenpo jogo = new Jokenpo();

    public Atende(Socket clienteSocket) {
        this.clienteSocket = clienteSocket;
        this.comunicacao = new Comunicacao(clienteSocket);
    }

    public void setOponente(Atende oponente) {
        this.oponente = oponente;
    }

    public void enviarMensagem(Mensagem msg) {
        comunicacao.send(msg);
    }

    @Override
    public void run() {
        boolean online = true;
        Mensagem mensagemRecebida;

        // Procurar por um oponente
        synchronized (Servidor.atendentes) {
            while (oponente == null && online) {
                for (Atende atende : Servidor.atendentes) {
                    if (atende != this && atende.oponente == null) {
                        this.oponente = atende;
                        atende.setOponente(this);
                        this.enviarMensagem(
                                new Mensagem("Servidor", "Oponente encontrado. Comece a jogar!"));
                        atende.enviarMensagem(
                                new Mensagem("Servidor", "Oponente encontrado. Comece a jogar!"));
                        break;
                    }
                }
                if (oponente == null) {
                    try {
                        Servidor.atendentes.wait();
                    } catch (InterruptedException e) {
                        online = false;
                    }
                }
            }
        }

        // Receber jogadas dos jogadores
        while (online) {
            mensagemRecebida = (Mensagem) comunicacao.receive();
            if (mensagemRecebida == null) {
                online = false;
            } else {
                jogadorNome = mensagemRecebida.getNome();
                jogadorJogada = mensagemRecebida.getTexto();

                synchronized (this) {
                    if (oponente.jogadorJogada == null) {
                        enviarMensagem(
                                new Mensagem("Servidor", "Aguardando jogada do oponente..."));
                    } else {
                        String resultado = jogo.jogar(new Mensagem(jogadorNome, jogadorJogada),
                                new Mensagem(oponente.jogadorNome, oponente.jogadorJogada));
                        Mensagem resultadoMensagem = new Mensagem(
                                "Jogo entre " + jogadorNome + " e " + oponente.jogadorNome,
                                "Resultado: " + jogadorJogada + " vs " + oponente.jogadorJogada
                                        + " -> " + resultado);
                        enviarMensagem(resultadoMensagem);
                        oponente.enviarMensagem(resultadoMensagem);
                        // Resetar estado para próximo jogo
                        this.jogadorJogada = null;
                        this.oponente.jogadorJogada = null;
                    }
                }
            }
        }

        // Remover da lista de atendentes e fechar a conexão
        synchronized (Servidor.atendentes) {
            Servidor.atendentes.remove(this);
            if (oponente != null) {
                Servidor.atendentes.remove(oponente);
            }
        }

        try {
            clienteSocket.close();
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}
