package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.ClearService;

import java.util.HashMap;
import java.util.Map;

public class ClearHandler {
    private final ClearService clearService;
    private final Gson gson = new Gson();

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

            Map<String, String> mappedResult = new HashMap<>();
            mappedResult.put("message", "Error: " + e.getMessage());
            var result = gson.toJson(mappedResult);

            ctx.result(result);
        }

    }
}
