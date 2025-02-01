import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Headers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class SimpleHttpServer {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        server.createContext("/subestacao", new SubestacaoHandler());
        server.setExecutor(null); 
        server.start();
        System.out.println("Servidor rodando na porta 8081...");
    }

    static class SubestacaoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Headers headers = exchange.getResponseHeaders();
            headers.add("Access-Control-Allow-Origin", "*");
            headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");
            headers.add("Access-Control-Allow-Headers", "Content-Type, Authorization");

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                handleGetRequest(exchange);
            } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                handlePostRequest(exchange);
            } else if ("PUT".equalsIgnoreCase(exchange.getRequestMethod())) {
                handlePutRequest(exchange);
            } else {
                exchange.sendResponseHeaders(405, -1); 
            }
        }

        private void handleGetRequest(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String[] segments = path.split("/");
            if (segments.length == 2) {
                final String URL = "jdbc:postgresql://localhost:5432/teste_sinapsis";
                final String USER = "postgres";
                final String PASSWORD = "senha para o banco";
                List<Map<String, Object>> subestacoesList = new ArrayList<>();
                try {
                    Class.forName("org.postgresql.Driver");
        
                    Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                    System.out.println("Conexão com o banco de dados bem-sucedida!");
        
                    String query = "SELECT * FROM TB_SUBESTACAO";
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(query);
        
                    while (resultSet.next()) {
                        Map<String, Object> subestacao = new HashMap<>();
                        subestacao.put("id", resultSet.getInt("ID_SUBESTACAO"));
                        subestacao.put("nome", resultSet.getString("NOME"));
                        subestacao.put("codigo", resultSet.getString("CODIGO"));
                        subestacao.put("latitude", resultSet.getDouble("LATITUDE"));
                        subestacao.put("longitude", resultSet.getDouble("LONGITUDE"));
                        subestacoesList.add(subestacao);
                    }
        
                    resultSet.close();
                    statement.close();
                    connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    exchange.sendResponseHeaders(500, -1); 
                    return;
                }
        
                StringBuilder jsonResponse = new StringBuilder("[");
                for (int i = 0; i < subestacoesList.size(); i++) {
                    Map<String, Object> subestacao = subestacoesList.get(i);
                    jsonResponse.append("{")
                            .append("\"id\":").append(subestacao.get("id")).append(",")
                            .append("\"nome\":\"").append(subestacao.get("nome")).append("\",")
                            .append("\"codigo\":\"").append(subestacao.get("codigo")).append("\",")
                            .append("\"latitude\":").append(subestacao.get("latitude")).append(",")
                            .append("\"longitude\":").append(subestacao.get("longitude"))
                            .append("}");
                    if (i < subestacoesList.size() - 1) {
                        jsonResponse.append(",");
                    }
                }
                jsonResponse.append("]");
        
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, jsonResponse.toString().getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(jsonResponse.toString().getBytes());
                os.close();
            } else if (segments.length == 3) {
                int id;
                try {
                    id = Integer.parseInt(segments[2]);
                } catch (NumberFormatException e) {
                    exchange.sendResponseHeaders(400, -1); 
                    return;
                }
        
                final String URL = "jdbc:postgresql://localhost:5432/teste_sinapsis";
                final String USER = "postgres";
                final String PASSWORD = "senha para o banco";
                Map<String, Object> subestacao = new HashMap<>();
                List<Map<String, Object>> redesList = new ArrayList<>();
                try {
                    Class.forName("org.postgresql.Driver");
        
                    Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                    System.out.println("Conexão com o banco de dados bem-sucedida!");
        
                    String querySubestacao = "SELECT * FROM TB_SUBESTACAO WHERE ID_SUBESTACAO = ?";
                    PreparedStatement preparedStatementSubestacao = connection.prepareStatement(querySubestacao);
                    preparedStatementSubestacao.setInt(1, id);
                    ResultSet resultSetSubestacao = preparedStatementSubestacao.executeQuery();
                    
                    if (resultSetSubestacao.next()) {
                        subestacao.put("id", resultSetSubestacao.getInt("ID_SUBESTACAO"));
                        subestacao.put("nome", resultSetSubestacao.getString("NOME"));
                        subestacao.put("codigo", resultSetSubestacao.getString("CODIGO"));
                        subestacao.put("latitude", resultSetSubestacao.getDouble("LATITUDE"));
                        subestacao.put("longitude", resultSetSubestacao.getDouble("LONGITUDE"));
                    } else {
                        exchange.sendResponseHeaders(404, -1); 
                        return;
                    }
                    String queryRedes = "SELECT * FROM TB_REDE_MT WHERE ID_SUBESTACAO = ?";
                    PreparedStatement preparedStatementRedes = connection.prepareStatement(queryRedes);
                    preparedStatementRedes.setInt(1, id);
                    ResultSet resultSetRedes = preparedStatementRedes.executeQuery();
                    while (resultSetRedes.next()) {
                        Map<String, Object> rede = new HashMap<>();
                        rede.put("id", resultSetRedes.getInt("ID_REDE_MT"));
                        rede.put("nome", resultSetRedes.getString("NOME"));
                        rede.put("codigo", resultSetRedes.getString("CODIGO"));
                        redesList.add(rede);
                    }
                    
                    resultSetSubestacao.close();
                    preparedStatementSubestacao.close();
                    resultSetRedes.close();
                    preparedStatementRedes.close();
                    connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    exchange.sendResponseHeaders(500, -1); 
                    return;
                }
        
                StringBuilder jsonResponse = new StringBuilder("{");
                jsonResponse.append("\"id\":").append(subestacao.get("id")).append(",")
                        .append("\"nome\":\"").append(subestacao.get("nome")).append("\",")
                        .append("\"codigo\":\"").append(subestacao.get("codigo")).append("\",")
                        .append("\"latitude\":").append(subestacao.get("latitude")).append(",")
                        .append("\"longitude\":").append(subestacao.get("longitude")).append(",")
                        .append("\"redes\":[");
                
                for (int i = 0; i < redesList.size(); i++) {
                    Map<String, Object> rede = redesList.get(i);
                    jsonResponse.append("{")
                            .append("\"id\":").append(rede.get("id")).append(",")
                            .append("\"nome\":\"").append(rede.get("nome")).append("\",")
                            .append("\"codigo\":\"").append(rede.get("codigo"))
                            .append("}");
                    if (i < redesList.size() - 1) {
                        jsonResponse.append(",");
                    }
                }

                jsonResponse.append("]}");
                
        
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, jsonResponse.toString().getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(jsonResponse.toString().getBytes());
                os.close();
                System.out.println(jsonResponse.toString());
            } else {
                exchange.sendResponseHeaders(405, -1); 
            }
        }
        private void handlePostRequest(HttpExchange exchange) throws IOException {
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                body.append(line);
            }
            br.close();
            isr.close();

            String requestBody = body.toString();
            String nome = extractJsonValue(requestBody, "nome");
            String codigo = extractJsonValue(requestBody, "codigo");
            double latitude = Double.parseDouble(extractJsonValue(requestBody, "latitude"));
            double longitude = Double.parseDouble(extractJsonValue(requestBody, "longitude"));

            final String URL = "jdbc:postgresql://localhost:5432/teste_sinapsis";
            final String USER = "postgres";
            final String PASSWORD = "senha para o banco";
            try {
                Class.forName("org.postgresql.Driver");

                Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Conexão com o banco de dados bem-sucedida!");

                String query = "INSERT INTO TB_SUBESTACAO (NOME, CODIGO, LATITUDE, LONGITUDE) VALUES (?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, nome);
                preparedStatement.setString(2, codigo);
                preparedStatement.setDouble(3, latitude);
                preparedStatement.setDouble(4, longitude);
                int rowsAffected = preparedStatement.executeUpdate();

                int subestacaoId = -1;
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    subestacaoId = generatedKeys.getInt(1);
                }

                String redesJson = requestBody.substring(requestBody.indexOf("\"redes\":") + 8);
                redesJson = redesJson.substring(0, redesJson.lastIndexOf("]") + 1);
                List<Map<String, String>> redesList = parseRedesJson(redesJson);

                for (Map<String, String> rede : redesList) {
                    String redeNome = rede.get("nome");
                    String redeCodigo = rede.get("codigo");

                    String redeQuery = "INSERT INTO TB_REDE_MT (NOME, CODIGO, ID_SUBESTACAO) VALUES (?, ?, ?)";
                    PreparedStatement redeStatement = connection.prepareStatement(redeQuery);
                    redeStatement.setString(1, redeNome);
                    redeStatement.setString(2, redeCodigo);
                    redeStatement.setInt(3, subestacaoId);
                    redeStatement.executeUpdate();
                    redeStatement.close();
                }

                preparedStatement.close();
                connection.close();

                if (rowsAffected > 0) {
                    String jsonResponse = "{"
                            + "\"id\":" + subestacaoId + ","
                            + "\"nome\":\"" + nome + "\","
                            + "\"codigo\":\"" + codigo + "\","
                            + "\"latitude\":" + latitude + ","
                            + "\"longitude\":" + longitude
                            + "}";

                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(201, jsonResponse.getBytes().length); 
                    OutputStream os = exchange.getResponseBody();
                    os.write(jsonResponse.getBytes());
                    os.close();
                } else {
                    exchange.sendResponseHeaders(500, -1); 
                }
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1); 
            }
        }

        private void handlePutRequest(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String[] segments = path.split("/");
            if (segments.length != 3) {
                exchange.sendResponseHeaders(400, -1); 
                return;
            }

            int id;
            try {
                id = Integer.parseInt(segments[2]);
            } catch (NumberFormatException e) {
                exchange.sendResponseHeaders(400, -1); 
                return;
            }

            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                body.append(line);
            }
            br.close();
            isr.close();

            String requestBody = body.toString();
            String nome = extractJsonValue(requestBody, "nome");
            String codigo = extractJsonValue(requestBody, "codigo");
            double latitude = Double.parseDouble(extractJsonValue(requestBody, "latitude"));
            double longitude = Double.parseDouble(extractJsonValue(requestBody, "longitude"));
            

            final String URL = "jdbc:postgresql://localhost:5432/teste_sinapsis";
            final String USER = "postgres";
            final String PASSWORD = "senha para o banco";
            try {
                Class.forName("org.postgresql.Driver");

                Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Conexão com o banco de dados bem-sucedida!");

                String query = "UPDATE TB_SUBESTACAO SET NOME = ?, CODIGO = ?, LATITUDE = ?, LONGITUDE = ? WHERE ID_SUBESTACAO = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, nome);
                preparedStatement.setString(2, codigo);
                preparedStatement.setDouble(3, latitude);
                preparedStatement.setDouble(4, longitude);
                preparedStatement.setInt(5, id);
                int rowsAffected = preparedStatement.executeUpdate();

                preparedStatement.close();
                connection.close();

                if (rowsAffected > 0) {
                    exchange.sendResponseHeaders(200, -1); 
                } else {
                    exchange.sendResponseHeaders(404, -1); 
                }
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1); 
            }
        }

        private String extractJsonValue(String json, String key) {
            String searchKey = "\"" + key + "\":";
            int startIndex = json.indexOf(searchKey) + searchKey.length();
            if (startIndex == searchKey.length() - 1) {
                return null; 
            }
            int endIndex = json.indexOf(",", startIndex);
            if (endIndex == -1) {
                endIndex = json.indexOf("}", startIndex);
            }
            String value = json.substring(startIndex, endIndex).trim();
            if (value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length() - 1);
            }
            return value;
        }

        private List<Map<String, String>> parseRedesJson(String redesJson) {
            List<Map<String, String>> redesList = new ArrayList<>();
            redesJson = redesJson.substring(1, redesJson.length() - 1); 
            String[] redesArray = redesJson.split("\\},\\{");

            for (String rede : redesArray) {
                rede = rede.replace("{", "").replace("}", ""); 
                String[] keyValuePairs = rede.split(",");
                Map<String, String> redeMap = new HashMap<>();
                for (String pair : keyValuePairs) {
                    String[] keyValue = pair.split(":");
                    String key = keyValue[0].trim().replace("\"", "");
                    String value = keyValue[1].trim().replace("\"", "");
                    redeMap.put(key, value);
                }
                redesList.add(redeMap);
            }
            return redesList;
        }
    }
}