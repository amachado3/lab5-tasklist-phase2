package cncs.academy.ess.controller;

import cncs.academy.ess.model.User;
import cncs.academy.ess.repository.UserRepository;
import cncs.academy.ess.service.TodoUserService;
import com.auth0.jwt.JWT;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.UnauthorizedResponse;
import org.casbin.jcasbin.main.Enforcer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthorizationMiddleware implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationMiddleware.class);
    private final UserRepository userRepository;

    private final TodoUserService userService;

    public AuthorizationMiddleware(UserRepository userRepository, TodoUserService userService) {

        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public void handle(Context ctx) throws Exception {
        if (ctx.header("Access-Control-Request-Headers") != null) {
            return;
        }

        if (ctx.path().equals("/user") && ctx.method().name().equals("POST")
        || ctx.path().equals("/login") && ctx.method().name().equals("POST")) {
            return;
        }
        // Check if authorization header exists
        String authorizationHeader = ctx.header("Authorization");
        String path = ctx.path();
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.info("Authorization header is missing or invalid '{}' for path '{}'", authorizationHeader, path);
            throw new UnauthorizedResponse();
        }

        // Extract token from authorization header
        String token = authorizationHeader.substring(7); // Remove "Bearer "

        // Check if token is valid (perform authentication logic)
        int userId = validateTokenAndGetUserId(ctx, token);
        if (userId == -1) {
            logger.info("Authorization token is invalid {}", token  );
            throw new UnauthorizedResponse();
        }

        // Add user ID to context for use in route handlers
        ctx.attribute("userId", userId);

        // Política RBAC
        Enforcer enforcer = new Enforcer("./src/main/resources/api-access-control/model.conf", "./src/main/resources/api-access-control/policy.csv");
        User user = userService.getUser(userId);
        if (!enforcer.enforce(user.getUsername(), ctx.path(), ctx.method().name())) {
            throw new UnauthorizedResponse();
        }
    }

    private int validateTokenAndGetUserId(Context cts, String token) {
        String username = JWT.decode(token).getClaim("username").asString();
        User user = userRepository.findByUsername(username);
        if (user == null) {
            // user not found, token is invalid
            return -1;
        }
        return user.getId();
    }
}

