package org.test;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.FluentProducerTemplate;
import org.eclipse.microprofile.faulttolerance.Fallback;

import java.util.List;

@ApplicationScoped
@Path("/measurements")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CoalConsumptionResource {
    @Inject
    CamelContext context;

    @EndpointInject(CoalConsumptionRoute.DIRECT_GET_COAL_CONSUMPTION)
    FluentProducerTemplate producerTemplate;

    /**
     * This API utilizes a Camel route to get coal consumption measurements from Tilastokeskus (Statistics Finland).
     *
     * If the Camel route returns an error, getMeasurementsFromDb method is used instead. The measurements stored in
     * the SQLite database are imaginary (and highly optimistic).
     *
     * @return
     */
    @GET
    @Transactional( Transactional.TxType.REQUIRES_NEW )
    @Fallback( fallbackMethod = "getMeasurementsFromDb" )
    public List<Measurement> getMeasurements() {
        return producerTemplate.to(CoalConsumptionRoute.DIRECT_GET_COAL_CONSUMPTION).request(List.class);
    }

    public List<Measurement> getMeasurementsFromDb() {
        return Measurement.listAll();
    }
}
