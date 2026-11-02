package org.example;

import java.sql.*;
import java.util.ArrayList;

public class TokenDAO {

    // Generate and save a new token
    public Token generateToken(String studentName) {

        int tokenNumber = 1;

        try (Connection connection = DBConnection.getConnection()) {

            String query = "SELECT COALESCE(MAX(token_number), 0) + 1 FROM tokens";

            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                tokenNumber = result.getInt(1);
            }

            String insertQuery =
                    "INSERT INTO tokens (token_number, student_name) VALUES (?, ?)";

            PreparedStatement insertStatement =
                    connection.prepareStatement(insertQuery);

            insertStatement.setInt(1, tokenNumber);
            insertStatement.setString(2, studentName);

            insertStatement.executeUpdate();

            return new Token(tokenNumber, studentName, "WAITING");

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get all tokens
    public ArrayList<Token> getAllTokens() {

        ArrayList<Token> tokenList = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection()) {

            String query =
                    "SELECT token_number, student_name, status FROM tokens ORDER BY token_number";

            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();

            while (result.next()) {

                Token token = new Token(
                        result.getInt("token_number"),
                        result.getString("student_name"),
                        result.getString("status")
                );

                tokenList.add(token);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tokenList;
    }

    // Serve the next waiting token
    public boolean serveNextToken() {

        try (Connection connection = DBConnection.getConnection()) {

            String query =
                    "SELECT id FROM tokens WHERE status = 'WAITING' ORDER BY token_number LIMIT 1";

            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();

            if (result.next()) {

                int id = result.getInt("id");

                String updateQuery =
                        "UPDATE tokens SET status = 'SERVED' WHERE id = ?";

                PreparedStatement updateStatement =
                        connection.prepareStatement(updateQuery);

                updateStatement.setInt(1, id);

                updateStatement.executeUpdate();

                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Cancel a token using its token number
    public boolean cancelToken(int tokenNumber) {

        try (Connection connection = DBConnection.getConnection()) {

            String query =
                    "UPDATE tokens SET status = 'CANCELLED' " +
                            "WHERE token_number = ? AND status = 'WAITING'";

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, tokenNumber);

            int rowsUpdated = statement.executeUpdate();

            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get total number of tokens
    public int getTotalTokens() {

        return getCount("SELECT COUNT(*) FROM tokens");
    }

    // Get number of waiting tokens
    public int getWaitingTokens() {

        return getCount(
                "SELECT COUNT(*) FROM tokens WHERE status = 'WAITING'"
        );
    }

    // Get number of served tokens
    public int getServedTokens() {

        return getCount(
                "SELECT COUNT(*) FROM tokens WHERE status = 'SERVED'"
        );
    }

    // Get the current waiting token number
    public int getNowServing() {

        try (Connection connection = DBConnection.getConnection()) {

            String query =
                    "SELECT token_number FROM tokens " +
                            "WHERE status = 'WAITING' " +
                            "ORDER BY token_number LIMIT 1";

            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getInt("token_number");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // Helper method to get counts from database
    private int getCount(String query) {

        try (Connection connection = DBConnection.getConnection()) {

            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}