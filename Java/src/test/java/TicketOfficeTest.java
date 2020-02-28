import com.github.jenspiegsa.wiremockextension.Managed;
import com.github.jenspiegsa.wiremockextension.WireMockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.github.jenspiegsa.wiremockextension.ManagedWireMockServer.with;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.BDDAssertions.then;

@DisplayName("WireMockExtension usage")
@ExtendWith(WireMockExtension.class)
public class TicketOfficeTest {

    @Managed
    WireMockServer server = with(wireMockConfig().dynamicPort());
    private String baseUrl;

    @Test
    public void reserveSeats() {

        String expectedBookingId = "75bcd15";
        givenThat(get("/booking_reference").willReturn(ok(expectedBookingId)));

        baseUrl = server.baseUrl();

        ReservationRequest request = new ReservationRequest("",123);
        TicketOffice ticketOffice = new TicketOffice(baseUrl);


        Reservation reservation = ticketOffice.makeReservation(request);


        then(reservation).isNotNull();
        then(reservation.bookingId).isEqualTo(expectedBookingId);
    }
}
