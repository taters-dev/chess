package server;

import com.google.gson.Gson;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

public class ExceptionHandler {
    private final Gson gson = new Gson();
    public ExceptionHandler(){

    }

    public void errorHelper(Context ctx, Exception e){
        Map<String, String> mappedResult = new HashMap<>();
        mappedResult.put("message", "Error: " + e.getMessage());
        var result = gson.toJson(mappedResult);

        ctx.result(result);
    }
}
