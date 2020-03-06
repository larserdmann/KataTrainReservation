import train.TrainData;
import train.TrainDataParser;

import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.List;
import java.util.Set;

public class TicketOffice {

    private String bookingRefBaseUrl;
    private String trainDataBaseUrl;
    private TrainDataParser trainDataParser;

    public TicketOffice(String baseUrl, String trainDataBaseUrl, TrainDataParser trainDataParser) {
        this.bookingRefBaseUrl = baseUrl;
        this.trainDataBaseUrl = trainDataBaseUrl;
        this.trainDataParser = trainDataParser;
    }

    public Reservation makeReservation(ReservationRequest request) {

        String bookingId = createBookingId();

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(UriBuilder.fromUri(trainDataBaseUrl).path("data_for_train").path(request.trainId));

        Response response = target.request().get();
        JsonObject json = response.readEntity(JsonObject.class);

        TrainData trainData = trainDataParser.parse(json);

        List<Seat> seats = null;

        return new Reservation(request.trainId, seats, bookingId);
    }

    private String createBookingId() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(UriBuilder.fromUri(bookingRefBaseUrl).path("booking_reference"));

        Response response = target.request().get();

        return response.readEntity(String.class);
    }

}