package com.scheduler;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Headers;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

public class ApiServer {

    private static final int PORT = 8080;

    public static void start(Scheduler scheduler) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Serve dashboard
        server.createContext("/", exchange -> {
            try {
                if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                    sendCorsOptions(exchange);
                    return;
                }

                String path = exchange.getRequestURI().getPath();

                if ("/".equals(path) || "/index.html".equals(path)) {
                    File f = new File("frontend/index.html");
                    if (f.exists()) {
                        byte[] bytes = readAllBytes(f);

                        setCors(exchange);
                        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                        exchange.sendResponseHeaders(200, bytes.length);

                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(bytes);
                        }
                        return;
                    }
                }

                setCors(exchange);
                send(exchange, 404, "Not Found");

            } catch (Exception e) {
                setCors(exchange);
                send(exchange, 500, "Server error");
            }
        });

        // POST /task
        server.createContext("/task", exchange -> {
            try {
                if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                    sendCorsOptions(exchange);
                    return;
                }

                if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {

                    Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());

                    int priority = Integer.parseInt(params.getOrDefault("priority", "5"));
                    int time = Integer.parseInt(params.getOrDefault("time", "500"));

                    int id = Database.saveTask(priority, time);

                    Task t = new Task(priority, time);
                    t.id = id;

                    scheduler.submit(t);

                    setCors(exchange);
                    send(exchange, 200, "Task Created: " + t);
                    return;
                }

                setCors(exchange);
                send(exchange, 405, "Use POST");

            } catch (Exception e) {
                setCors(exchange);
                send(exchange, 500, "Server error");
            }
        });

        // GET /tasks
        server.createContext("/tasks", exchange -> {
            try {
                if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                    sendCorsOptions(exchange);
                    return;
                }

                if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {

                    List<String> result = new ArrayList<>();

                    try (Connection conn = Database.getConnection();
                         PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tasks");
                         ResultSet rs = stmt.executeQuery()) {

                        while (rs.next()) {
                            result.add(
                                "Task-" + rs.getInt("id")
                                + "(priority=" + rs.getInt("priority")
                                + ",ms=" + rs.getInt("processing_ms")
                                + ",status=" + rs.getString("status") + ")"
                            );
                        }
                    }

                    setCors(exchange);
                    send(exchange, 200, String.join("\n", result));
                    return;
                }

                setCors(exchange);
                send(exchange, 405, "Use GET");

            } catch (Exception e) {
                setCors(exchange);
                send(exchange, 500, "Server error");
            }
        });

        // GET /workers
        server.createContext("/workers", exchange -> {
            try {
                if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                    sendCorsOptions(exchange);
                    return;
                }

                if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {

                    StringBuilder sb = new StringBuilder("Workers:\n");

                    scheduler.workers.forEach(w ->
                        sb.append(w.name)
                          .append(" | Load=")
                          .append(w.getLoad())
                          .append("\n")
                    );

                    setCors(exchange);
                    send(exchange, 200, sb.toString());
                    return;
                }

                setCors(exchange);
                send(exchange, 405, "Use GET");

            } catch (Exception e) {
                setCors(exchange);
                send(exchange, 500, "Server error");
            }
        });

        server.start();
        System.out.println("[API] Running at http://localhost:" + PORT);
    }

    private static void setCors(HttpExchange ex) {
        Headers h = ex.getResponseHeaders();
        h.set("Access-Control-Allow-Origin", "*");
        h.set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        h.set("Access-Control-Allow-Headers", "Content-Type, Accept");
    }

    private static void sendCorsOptions(HttpExchange ex) throws IOException {
        setCors(ex);
        ex.sendResponseHeaders(204, -1);
        ex.close();
    }

    private static void send(HttpExchange ex, int code, String msg) {
        try {
            byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
            ex.sendResponseHeaders(code, bytes.length);

            try (OutputStream os = ex.getResponseBody()) {
                os.write(bytes);
            }

        } catch (Exception e) { }
    }

    private static byte[] readAllBytes(File f) throws IOException {
        try (FileInputStream fis = new FileInputStream(f)) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            int r;
            while ((r = fis.read(buf)) != -1) bos.write(buf, 0, r);
            return bos.toByteArray();
        }
    }

    private static Map<String, String> queryToMap(String q) {
        if (q == null) return Map.of();

        Map<String, String> map = new HashMap<>();
        for (String p : q.split("&")) {
            String[] parts = p.split("=");

            if (parts.length == 2)
                map.put(parts[0], parts[1]);
        }

        return map;
    }
}



