package com.scheduler;

import java.sql.*;

public class Database {

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection("jdbc:sqlite:scheduler.db");
    }

    public static void init() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS tasks (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    priority INTEGER,
                    processing_ms INTEGER,
                    status TEXT
                )
            """);

            System.out.println("[DB] Initialized successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int saveTask(int priority, int time) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO tasks(priority, processing_ms, status) VALUES(?,?,?)",
                 Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, priority);
            stmt.setInt(2, time);
            stmt.setString(3, "PENDING");
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void updateStatus(int id, String status) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "UPDATE tasks SET status=? WHERE id=?")) {

            stmt.setString(1, status);
            stmt.setInt(2, id);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   public static void clearTasks() {
    try (Connection conn = getConnection();
         Statement stmt = conn.createStatement()) {

        // delete all rows
        stmt.execute("DELETE FROM tasks");

        // reset auto-increment sequence
        stmt.execute("DELETE FROM sqlite_sequence WHERE name='tasks'");

        System.out.println("[DB] All tasks cleared and ID reset to 1.");
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}

