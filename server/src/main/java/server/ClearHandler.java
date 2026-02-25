package server;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.ClearService;

public class ClearHandler {
    private final ClearService clearService;
    private final ExceptionHandler exceptionHandler = new ExceptionHandler();


    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    public void handleClear(Context ctx) {
        try {
            clearService.clear();

            ctx.status(200);
            ctx.result("{}");

        } catch (DataAccessException e){
            ctx.status(500);
            exceptionHandler.errorHelper(ctx, e);
        }

    }
}
