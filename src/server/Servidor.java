package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Servidor {
    protected static ArrayList<Atende> atendentes = new ArrayList<>();

    public static void main(String[] args) {
        final int PORTA = 9876;
        ServerSocket serverSocket;
        Socket clienteSocket;

        try {
            serverSocket = new ServerSocket(PORTA);
            System.out.println("Servidor iniciado na porta " + PORTA + ". Esperando conexões...");

            while (true) {
                clienteSocket = serverSocket.accept(); // Aguarda a conexão de um cliente
                System.out.println(
                        "Cliente conectado: " + clienteSocket.getInetAddress().getHostAddress());
                Atende atende = new Atende(clienteSocket);
                synchronized (atendentes) { // Sincroniza a lista de atendentes
                    atendentes.add(atende);
                    atendentes.notifyAll(); // Notifica todas as threads esperando por um oponente
                }
                atende.start(); // Inicia a thread de atendimento
            }

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}
