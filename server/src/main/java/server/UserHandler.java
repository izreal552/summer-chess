package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.UserService;
import spark.Request;
import spark.Response;

public class UserHandler {
    UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public Object register(Request request, Response response) throws DataAccessException {
        UserData userData = new Gson().fromJson(request.body(), UserData.class);

        if (userData.username() == null || userData.password() == null || userData.email() == null) {
            throw new BadRequestException("No username/password given");
        }

        try {
            AuthData authData = userService.createUser(userData);
            response.status(200);
            return new Gson().toJson(authData);
        } catch (BadRequestException e) {
            if (e.getMessage().contains("already exists")) {
                response.status(403);
                return "{ \"message\": \"Error: already taken\" }";
            } else {
                response.status(500);
                return "{ \"message\": \"Error: " + e.getMessage() + "\" }";
            }
        } catch (DataAccessException e) {
            response.status(500);
            return "{ \"message\": \"Error: " + e.getMessage() + "\" }";
        }

    }

    public Object login(Request req, Response resp){
        try {
            UserData userData = new Gson().fromJson(req.body(), UserData.class);
            if (userData == null || userData.username() == null || userData.password() == null) {
                resp.status(400);
                return "{ \"message\": \"Error: Missing required fields\" }";
            }

            AuthData authData = userService.loginUser(userData);
            resp.status(200);
            return new Gson().toJson(authData);
        } catch (JsonSyntaxException e) {
            resp.status(400);
            return "{ \"message\": \"Error: Invalid request format\" }";
        } catch (DataAccessException e) {
            if (e.getMessage().contains("Invalid credentials") || e.getMessage().contains("User not found")) {
                resp.status(401);
                return "{ \"message\": \"Error: Unauthorized\" }";
            } else {
                resp.status(500);
                return "{ \"message\": \"Error: " + e.getMessage() + "\" }";
            }
        } catch (Exception e) {
            resp.status(500);
            return "{ \"message\": \"Error: Internal server error\" }";
        }
    }

    public Object logout(Request request, Response response) {
        try {
            String authToken = request.headers("authorization");
            if (authToken == null || authToken.trim().isEmpty()) {
                response.status(401);
                return "{ \"message\": \"Error: unauthorized\" }";
            }

            try {
                userService.logoutUser(authToken);
                response.status(200);
                return "{}";
            } catch (DataAccessException e) {
                String msg = e.getMessage();
                if (msg != null && (e.getMessage().contains("does not exist") || e.getMessage().contains("database operation"))) {
                    response.status(401);
                    return "{ \"message\": \"Error: unauthorized\" }";
                } else {
                    response.status(500);
                    return "{ \"message\": \"Error: " + (msg != null ? msg : "internal server error") + "\" }";
                }
            }
        } catch (Exception e) {
            response.status(500);
            return "{ \"message\": \"Error: " + e.getMessage() + "\" }";
        }
    }
}
