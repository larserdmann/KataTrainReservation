package train;

import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.List;

public class TicketOffice {

    private String bookingRefBaseUrl;
    private String trainDataBaseUrl;
    private TrainDataParser trainDataParser;
    private ReservationService reservationService;

    public TicketOffice(String baseUrl, String trainDataBaseUrl, TrainDataParser trainDataParser, ReservationService reservationService) {
        this.bookingRefBaseUrl = baseUrl;
        this.trainDataBaseUrl = trainDataBaseUrl;
        this.trainDataParser = trainDataParser;
        this.reservationService = reservationService;
    }

    public Reservation makeReservation(ReservationRequest request) {

        String bookingReference = createBookingReference();

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(UriBuilder.fromUri(trainDataBaseUrl).path("data_for_train").path(request.trainId));

        Response response = target.request().get();
        JsonObject json = response.readEntity(JsonObject.class);

        TrainData trainData = trainDataParser.parse(json);

        // business logic (70% ... )
        List<Seat> seats = reservationService.tryToReserve(request, trainData, bookingReference);

        // POST req.
        Reservation reservation = new Reservation(request.trainId, seats, bookingReference);


        return reservation;
    }

    private String createBookingReference() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(UriBuilder.fromUri(bookingRefBaseUrl).path("booking_reference"));

        Response response = target.request().get();

        return response.readEntity(String.class);
    }

}