package client;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import util.Mensagem;

public class Cliente {
    private Socket socket;
    private Escuta escuta;
    private ObjectOutputStream out;

    public void start() {
        try (Scanner scanner = new Scanner(System.in)) {

            System.out.print("Digite o IP do servidor (padrão 127.0.0.1): ");
            String ip = scanner.nextLine();
            if (ip.isEmpty()) {
                ip = "127.0.0.1";
            }

            System.out.print("Digite a porta do servidor (padrão 9876): ");
            int porta = scanner.nextLine().isEmpty() ? 9876 : Integer.parseInt(scanner.nextLine());

            System.out.print("Digite seu nome de usuário: ");
            String username = scanner.nextLine();

            try {
                socket = new Socket(ip, porta);
                out = new ObjectOutputStream(socket.getOutputStream());
                escuta = new Escuta(socket);
                escuta.start();

                System.out.println("Conectado ao servidor.");
                System.out.println("Informe sua jogada: pedra, papel ou tesoura?");

                while (true) {
                    String playerInput = scanner.nextLine();
                    processarEntrada(playerInput, username);
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private void processarEntrada(String player, String username) {
        player = player.toLowerCase();

        switch (player) {
            case "pedra":
            case "papel":
            case "tesoura":
                Mensagem msg = new Mensagem(username, player);
                try {
                    out.writeObject(msg);
                } catch (Exception e) {
                    System.out.println("Erro: " + e.getMessage());
                }
                break;
            default:
                System.out.println("Entrada inválida, tente novamente.");
                break;
        }
    }

    public static void main(String[] args) {
        Cliente client = new Cliente();
        client.start();
    }
}
