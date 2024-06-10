package server;

import java.net.Socket;
import util.Comunicacao;
import util.Mensagem;

public class Atende extends Thread {
    Socket cliente;
    Comunicacao comunicacao;

    public Atende(Socket cliente) {
        this.cliente = cliente;
        comunicacao = new Comunicacao(cliente);
    }

    public void enviarMsg(Mensagem msg) {
        comunicacao.send(msg);
    }

    @Override
    public void run() {
        boolean online = true;
        Mensagem msg;

        while (online) {
            msg = (Mensagem) comunicacao.receive();
            if (msg == null) {
                online = false;
            } else {
                for (Atende atende : Servidor.atendentes) {
                    atende.enviarMsg(msg);
                }
            }
        }
        System.out.println("Final da thread");
        Servidor.atendentes.remove(this);
    }
}
