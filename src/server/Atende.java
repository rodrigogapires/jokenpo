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

    private int vitorias = 0;
    private int derrotas = 0;
    private int empates = 0;
    private boolean jogarContraCPU = false;

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

        // Receber tipo de jogo (contra CPU ou contra jogador)
        mensagemRecebida = (Mensagem) comunicacao.receive();
        if (mensagemRecebida != null && mensagemRecebida.getTexto().equals("cpu")) {
            jogarContraCPU = true;
            enviarMensagem(new Mensagem("Servidor",
                    "Você escolheu jogar contra a CPU. Informe sua jogada: pedra, papel ou tesoura?"));
        } else {
            // Procurar por um oponente
            synchronized (Servidor.atendentes) {
                while (oponente == null && online) {
                    for (Atende atende : Servidor.atendentes) {
                        if (atende != this && atende.oponente == null && !atende.jogarContraCPU) {
                            this.oponente = atende;
                            atende.setOponente(this);
                            this.enviarMensagem(new Mensagem("Servidor",
                                    "Oponente encontrado. Informe sua jogada: pedra, papel ou tesoura?"));
                            atende.enviarMensagem(new Mensagem("Servidor",
                                    "Oponente encontrado. Informe sua jogada: pedra, papel ou tesoura?"));
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
        }

        // Receber jogadas dos jogadores
        while (online) {
            mensagemRecebida = (Mensagem) comunicacao.receive();
            if (mensagemRecebida == null) {
                online = false;
            } else {
                jogadorNome = mensagemRecebida.getNome();
                jogadorJogada = mensagemRecebida.getTexto();

                if (jogarContraCPU) {
                    // Jogo contra a CPU
                    String resultado =
                            jogo.jogarContraCPU(new Mensagem(jogadorNome, jogadorJogada));
                    Mensagem resultadoMensagem = new Mensagem("Jogo contra CPU",
                            "Resultado: " + jogadorJogada + " vs " + "CPU" + " -> " + resultado);

                    // Atualizar estatísticas
                    if (resultado.equals("empate")) {
                        this.empates++;
                    } else if (resultado.equals(jogadorNome)) {
                        this.vitorias++;
                    } else {
                        this.derrotas++;
                    }

                    enviarMensagem(resultadoMensagem);
                    enviarMensagem(new Mensagem("Servidor",
                            "Vitórias: " + vitorias + ", Derrotas: " + derrotas + ", Empates: "
                                    + empates + "\nInforme sua jogada: pedra, papel ou tesoura?"));

                    // Resetar estado para próximo jogo
                    this.jogadorJogada = null;
                } else {
                    // Jogo contra outro jogador
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

                            // Atualizar estatísticas
                            if (resultado.equals("empate")) {
                                this.empates++;
                                oponente.empates++;
                            } else if (resultado.equals(jogadorNome)) {
                                this.vitorias++;
                                oponente.derrotas++;
                            } else {
                                this.derrotas++;
                                oponente.vitorias++;
                            }

                            enviarMensagem(resultadoMensagem);
                            oponente.enviarMensagem(resultadoMensagem);

                            // Enviar estatísticas atualizadas
                            enviarMensagem(new Mensagem("Servidor",
                                    "Vitórias: " + vitorias + ", Derrotas: " + derrotas
                                            + ", Empates: " + empates
                                            + "\nInforme sua jogada: pedra, papel ou tesoura?"));
                            oponente.enviarMensagem(new Mensagem("Servidor",
                                    "Vitórias: " + oponente.vitorias + ", Derrotas: "
                                            + oponente.derrotas + ", Empates: " + oponente.empates
                                            + "\nInforme sua jogada: pedra, papel ou tesoura?"));

                            // Resetar estado para próximo jogo
                            this.jogadorJogada = null;
                            this.oponente.jogadorJogada = null;
                        }
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
