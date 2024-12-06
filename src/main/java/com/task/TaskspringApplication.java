package com.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@RestController
public class TaskspringApplication {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        SpringApplication.run(TaskspringApplication.class, args);
    }

    @PostMapping("/tasks")
    public Map<String, String> addTask(@RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        String description = payload.getOrDefault("description", ""); // Handle missing description

        // create table if does not exist
        jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS tasks (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255) NOT NULL, description TEXT)");

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Task name is required");
        }

        jdbcTemplate.update("INSERT INTO tasks (name, description) VALUES (?, ?)", name, description);
        return Map.of("message", "Task added");
    }

    @GetMapping("/tasks")
    public List<Map<String, Object>> getTasks() {
        return jdbcTemplate.queryForList("SELECT * FROM tasks");

    }

    @PutMapping("/tasks/{id}")
    public Map<String, String> updateTask(@PathVariable("id") int id, @RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        String description = payload.getOrDefault("description", "");

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Task name is required");
        }

        int rowsAffected = jdbcTemplate.update("UPDATE tasks SET name = ?, description = ? WHERE id = ?", name,
                description, id);
        if (rowsAffected == 0) {
            throw new IllegalArgumentException("Task not found"); // Or return 404 Not Found
        }

        return Map.of("message", "Task updated");
    }

    @DeleteMapping("/tasks/{id}")
    public Map<String, String> deleteTask(@PathVariable("id") int id) {
        int rowsAffected = jdbcTemplate.update("DELETE FROM tasks WHERE id = ?", id);
        if (rowsAffected == 0) {
            throw new IllegalArgumentException("Task not found"); // or return 404
        }
        return Map.of("message", "Task with id " + id + " deleted");

    }
}
