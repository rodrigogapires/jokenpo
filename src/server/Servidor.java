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

            while (true) {
                System.out.println("Aguardando o cliente...");
                clienteSocket = serverSocket.accept();
                System.out.println(
                        "Cliente conectado: " + clienteSocket.getInetAddress().getHostAddress());
                Atende atende = new Atende(clienteSocket);
                atendentes.add(atende);
                atende.start();
            }

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}
