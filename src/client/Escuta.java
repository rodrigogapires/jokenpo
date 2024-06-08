package client;

import java.io.ObjectInputStream;
import java.net.Socket;

import util.Mensagem;

public class Escuta extends Thread {
    private Socket socket;
    private ObjectInputStream in;

    public Escuta(Socket socket) {
        this.socket = socket;
        try {
            in = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            System.out.println("Erro ao inicializar Escuta: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            Object serverMessage;
            while ((serverMessage = in.readObject()) != null) {
                if (serverMessage instanceof Mensagem) {
                    Mensagem msg = (Mensagem) serverMessage;
                    System.out.println("Server: " + msg.getTexto());
                } else {
                    System.out.println("Mensagem desconhecida recebida.");
                }
            }
        } catch (Exception e) {
            System.out.println("Erro em Escuta: " + e.getMessage());
        } finally {
            try {
                in.close();
                socket.close();
            } catch (Exception e) {
                System.out.println("Erro ao fechar recursos em Escuta: " + e.getMessage());
            }
        }
    }
}
