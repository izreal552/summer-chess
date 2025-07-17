package server;

import dataaccess.*;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {
    private final UserDAO userDAO = new SQLUserDAO();
    private final AuthDAO authDAO = new SQLAuthDAO();
    private final GameDAO gameDAO = new SQLGameDAO();

    private final UserService userService = new UserService(userDAO, authDAO);
    private final GameService gameService = new GameService(gameDAO, authDAO);

    private final UserHandler userHandler = new UserHandler(userService);
    private final GameHandler gameHandler = new GameHandler(gameService);

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        registerRoutes();
        registerExceptionHandlers();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public void clearDB() {
        userService.clear();
        gameService.clear();
    }

    private void registerRoutes() {
        // DB management
        Spark.delete("/db", this::handleClearDB);

        // User endpoints
        Spark.post("/user", userHandler::register);
        Spark.post("/session", userHandler::login);
        Spark.delete("/session", userHandler::logout);

        // Game endpoints
        Spark.get("/game", gameHandler::listGames);
        Spark.post("/game", gameHandler::createGame);
        Spark.put("/game", gameHandler::joinGame);
    }

    private void registerExceptionHandlers() {
        Spark.exception(BadRequestException.class, (ex, req, res) ->
                formatErrorResponse(res, 400, "bad request"));

        Spark.exception(UnauthorizedException.class, (ex, req, res) ->
                formatErrorResponse(res, 401, "unauthorized"));

        Spark.exception(Exception.class, (ex, req, res) -> {
            String message = ex.getMessage() != null ? ex.getMessage() : "internal server error";
            formatErrorResponse(res, 500, message);
        });
    }

    private Object handleClearDB(Request req, Response res) {
        clearDB();
        res.status(200);
        return "{}";
    }

    private void formatErrorResponse(Response response, int status, String message) {
        response.status(status);
        response.body("{ \"message\": \"Error: " + message + "\" }");
    }
}
