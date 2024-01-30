package org.test;

import java.util.*;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangeProperty;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http.HttpMethods;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Camel route definitions.
 */
@ApplicationScoped
public class CoalConsumptionRoute extends RouteBuilder {
    public static final String DIRECT_GET_COAL_CONSUMPTION = "direct:getCoalConsumption";
    @ConfigProperty(name = "start.year")
    Integer startYear;

    @ConfigProperty(name = "end.year")
    Integer endYear;

    @Override
    public void configure() {
        // The route has gotten messed up! Fix the route to get accurate data on coal consumption.

        // 'from' endpoint starts a Camel route (can be a timer, an API call, ...).
        // In this case, we use from(direct:...) to call the route from the same camel context (from CoalConsumptionResource)
        from(DIRECT_GET_COAL_CONSUMPTION)

                // Logging helps us follow the execution of the route
                // In this case, we log a message first thing after the route has started
                .log("Fetching coal consumption in Finland between 1970-2022")

                // Logging helps us follow the execution of the route
                // In this case, we log an exchange property once it has been set
                .log("Values: ${exchangeProperty.values}")

                // Exchange properties are a part of the Camel exchange and can be used to store information needed later on the Camel route
                // In this case, we save the coal consumption values from the JSON response as an exchange property
                .setProperty("values", jsonpath("$.value"))

                // 'bean' is used to execute Java code
                // In this case, we execute a method called getQuery which returns a String we need for the post request
                // The returned String is automatically set as the Camel message body
                .bean(this, "getQuery")

                // Headers are a part of the Camel message and can be used to e.g. deliver information to endpoints
                // In this case, we set headers needed for post request
                .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.POST))
                .setHeader(HttpHeaders.CONTENT_TYPE, constant(ContentType.APPLICATION_JSON))

                // 'bean' is used to execute Java code
                // In this case, we execute a method called mapData which takes the coal consumption values and returns a list of Measurements
                .bean(this, "mapData")

                // 'to' endpoint determines what the route does next
                // In this case, we send a post request to Statistics Finland's database.
                // After the post request, the Camel message body contains the JSON response
                .to("https://pxdata.stat.fi:443/PxWeb/api/v1/en/StatFin/kivih/statfin_kivih_pxt_11l7.px")
        ;
    }

    public String getQuery() {
        return "{\"query\": [{\"code\": \"Tiedot\", \"selection\": { \"filter\": \"item\", \"values\": [ \"kulutus_t\"]}}],\"response\": {\"format\": \"json-stat2\"}}";
    }

    public List<Measurement> mapData(@ExchangeProperty("values") List<Integer> values) {
        List<Measurement> list = new ArrayList<>();
        if (values != null && values.size() == endYear - (startYear - 1)) {
            for (int i = startYear; i <= endYear; i++) {
                Measurement measurement = new Measurement();
                measurement.setYear(i);
                measurement.setCoalConsumption(values.get(i-(startYear)));
                list.add(measurement);
            }
        }
        return list;
    }
}
