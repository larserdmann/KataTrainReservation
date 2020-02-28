import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.List;

public class TicketOffice {

    private String baseUrl;

    public TicketOffice(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Reservation makeReservation(ReservationRequest request) {

        // Call booking_reference api here
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(UriBuilder.fromUri(baseUrl).path("booking_reference"));

        Response response = target.request().get();

        String bookingId = response.readEntity(String.class);
        List<Seat> seats = null;


        return new Reservation(request.trainId, seats, bookingId);
    }

}