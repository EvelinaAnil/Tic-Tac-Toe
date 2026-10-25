package framework.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class ServerConnection {
    private final String host;
    private final int port;
    private final String username;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean running = false;
    private ServerListener listener;

    public ServerConnection(String host, int port, String username) {
        this.host = host;
        this.port = port;
        this.username = username;
    }

    public void setListener(ServerListener listener) {
        this.listener = listener;
    }

    public void connect() throws IOException {
        this.socket = new Socket(host, port);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.running = true;

        Thread listenerThread = new Thread(this::listen);
        listenerThread.setDaemon(true);
        listenerThread.start();

        // Direct inloggen volgens schoolprotocol
        send("login " + username);
        if (listener != null) {
            listener.onConnected();
        }
    }

    public synchronized void send(String command) {
        System.out.println("C: " + command);
        if (out != null) {
            out.println(command);
        }
    }

    public void subscribe(String gameType) {
        send("subscribe " + gameType);
    }

    public void sendMove(int move) {
        send("move " + move);
    }

    public void forfeit() {
        send("forfeit");
    }

    private void listen() {
        try {
            String line;
            while (running && (line = in.readLine()) != null) {
                line = line.trim();
                System.out.println("S: " + line);
                handleLine(line);
            }
        } catch (IOException e) {
            if (running && listener != null) {
                listener.onError("Verbinding verbroken: " + e.getMessage());
            }
        }
    }

    private void handleLine(String line) {
        if (listener == null) return;

        if (line.startsWith("ERR")) {
            listener.onError(line);
        } else if (line.startsWith("SVR GAMELIST")) {
            listener.onGameListReceived(ProtocolParser.parseList(line));
        } else if (line.startsWith("SVR PLAYERLIST")) {
            listener.onPlayerListReceived(ProtocolParser.parseList(line));
        } else if (line.startsWith("SVR GAME MATCH")) {
            Map<String, String> data = ProtocolParser.parseMap(line);
            String playerToMove = data.get("PLAYERTOMOVE");
            String opponent = data.get("OPPONENT");
            String gameType = data.get("GAMETYPE");
            boolean starting = playerToMove != null && playerToMove.equalsIgnoreCase(username);
            listener.onMatchStarted(gameType, opponent, starting);
        } else if (line.startsWith("SVR GAME YOURTURN")) {
            Map<String, String> data = ProtocolParser.parseMap(line);
            listener.onYourTurn(data.getOrDefault("TURNMESSAGE", ""));
        } else if (line.startsWith("SVR GAME MOVE")) {
            Map<String, String> data = ProtocolParser.parseMap(line);
            String player = data.get("PLAYER");
            int move = Integer.parseInt(data.get("MOVE"));
            String details = data.getOrDefault("DETAILS", "");
            listener.onMoveReceived(player, move, details);
        } else if (line.startsWith("SVR GAME WIN") ||
                line.startsWith("SVR GAME LOSS") ||
                line.startsWith("SVR GAME DRAW")) {
            Map<String, String> data = ProtocolParser.parseMap(line);
            String result = line.contains("WIN") ? "WIN" : (line.contains("LOSS") ? "LOSS" : "DRAW");
            listener.onGameOver(result, data.getOrDefault("COMMENT", ""));
        } else if (line.startsWith("SVR GAME CHALLENGE CANCELLED")) {
            Map<String, String> data = ProtocolParser.parseMap(line);
            listener.onChallengeCancelled(data.get("CHALLENGENUMBER"));
        } else if (line.startsWith("SVR GAME CHALLENGE")) {
            Map<String, String> data = ProtocolParser.parseMap(line);
            listener.onChallengeReceived(data.get("CHALLENGER"), data.get("GAMETYPE"), data.get("CHALLENGENUMBER"));
        }
    }

    public void sendMessage(String text) {
        String clean = text.replace("\n", " ").replace("\r", " ").replace("\"", "'");
        if (clean.length() > 128) {
            clean = clean.substring(0, 128);
        }
        send("message \"" + clean + "\"");
    }

    public void disconnect() {
        running = false;
        send("logout");
        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
    }
}