import com.google.common.collect.ImmutableList;
import org.codehaus.jackson.map.ObjectMapper;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;
import utils.ExcelStreamReader;
import utils.General;
import utils.ScheduledTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;

import static spark.Spark.*;

public final class Server {

    private Server() {
    }

    public static void main(final String... args) {

        staticFiles.location("/public");
        port(General.isInsideContainer() ? 80 : 4567);

        final Timer time = new Timer();
        time.schedule(new ScheduledTask(), ScheduledTask.MINUTE / 60, ScheduledTask.HOUR);

        get("/", (request, response) -> {
            final Map<String, Object> attributes = new HashMap<>();
            attributes.put("version", System.currentTimeMillis());
            attributes.put("isLoaded", ExcelStreamReader.getInstance().isLoaded());
            attributes.put("lastUpdate", ExcelStreamReader.getInstance().getLastUpdate());
            attributes.put("lastSuccess", ExcelStreamReader.getInstance().getLastSuccess());
            attributes.put("models", ExcelStreamReader.getInstance().getModels());
            attributes.put("records", ExcelStreamReader.getInstance().getRecords());

            return new ModelAndView(attributes, "landing.ftl");
        }, new FreeMarkerEngine());

        post("/status", (request, response) -> {
            final Map<String, Object> payload = new HashMap<>();

            final Optional<String> model = General.getRequestParameter(request, "model");
            final Optional<String> id = General.getRequestParameter(request, "id");
            final Optional<String> rma = General.getRequestParameter(request, "rma");

            if (rma.isPresent()) {
                payload.put("history", ExcelStreamReader.getInstance().getByRMA(rma.get()));
            } else if (model.isPresent() && id.isPresent()) {
                payload.put("history", ExcelStreamReader.getInstance().getDevice(model.get(), id.get()));
            } else {
                payload.put("history", ImmutableList.of());
            }

            return new ObjectMapper().writeValueAsString(payload);
        });
    }
}
