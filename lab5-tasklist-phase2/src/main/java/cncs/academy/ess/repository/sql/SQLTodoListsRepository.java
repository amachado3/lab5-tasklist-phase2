package cncs.academy.ess.repository.sql;

import cncs.academy.ess.model.TodoList;
import cncs.academy.ess.repository.TodoListsRepository;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLTodoListsRepository implements TodoListsRepository {
    private final BasicDataSource dataSource;

    public SQLTodoListsRepository(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public TodoList findById(int todoId) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM lists WHERE id = ?");
            stmt.setInt(1, todoId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToTodoList(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find list by ID", e);
        }
        return null;
    }

    @Override
    public List<TodoList> findAll() {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM lists");
            ResultSet rs = stmt.executeQuery();
            List<TodoList> lists = new ArrayList<>();
            while (rs.next()) {
                lists.add(mapResultSetToTodoList(rs));
            }
            return lists;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all lists", e);
        }
    }

    @Override
    public List<TodoList> findAllByUserId(int userId){
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM lists WHERE userId = ?");
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            List<TodoList> lists = new ArrayList<>();
            while (rs.next()) {
                lists.add(mapResultSetToTodoList(rs));
            }
            return lists;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all lists", e);
        }
    }

    @Override
    public int save(TodoList todoList) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO lists (name, ownerId) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, todoList.getName());
            stmt.setInt(2, todoList.getOwnerId());
            stmt.executeUpdate();
            int generatedId = 0;
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                generatedId = rs.getInt(1);
            }
            return generatedId;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save list", e);
        }
    }

    @Override
    public void update(TodoList todoList){
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE lists (name, ownerId) VALUES (?, ?) WHERE id = ?",
                    Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, todoList.getName());
            stmt.setInt(2, todoList.getOwnerId());
            stmt.setInt(3, todoList.getListId());
            stmt.executeUpdate();
            //podia retornar um boolean ou a lista
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save list", e);
        }
    }

    @Override
    public boolean deleteById(int listId) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM lists WHERE id = ?");
            stmt.setInt(1, listId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete list", e);
        }
    }

    private TodoList mapResultSetToTodoList(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        int ownerId = rs.getInt("ownerId");
        return new TodoList(id, name, ownerId);
    }
}