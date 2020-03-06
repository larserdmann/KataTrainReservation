import com.github.jenspiegsa.wiremockextension.Managed;
import com.github.jenspiegsa.wiremockextension.WireMockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import train.TrainData;
import train.TrainDataParser;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.github.jenspiegsa.wiremockextension.ManagedWireMockServer.with;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("WireMockExtension usage")
@ExtendWith(WireMockExtension.class)
@ExtendWith(MockitoExtension.class)
public class TicketOfficeTest {

    @Managed
    WireMockServer bookingRefServer = with(wireMockConfig().dynamicPort());
    @Managed
    WireMockServer trainDataServer = with(wireMockConfig().dynamicPort());
    @Mock
    TrainDataParser trainDataParser;

    private String bookingRefBaseUrl;
    private String trainDataBaseUrl;

    @Test
    public void reserveSeats() {

        String expectedBookingId = "75bcd15";
        String json = fromFile("express_2000_response.json");

        bookingRefServer.givenThat(get("/booking_reference").willReturn(ok(expectedBookingId)));
        trainDataServer.givenThat(get("/data_for_train/express_2000").willReturn(okJson(json)));

        given(trainDataParser.parse(any())).willReturn(new TrainData());

        bookingRefBaseUrl = bookingRefServer.baseUrl();
        trainDataBaseUrl = trainDataServer.baseUrl();

        ReservationRequest request = new ReservationRequest("express_2000", 123);
        TicketOffice ticketOffice = new TicketOffice(bookingRefBaseUrl, trainDataBaseUrl, trainDataParser);


        Reservation reservation = ticketOffice.makeReservation(request);


        then(reservation).isNotNull();
        then(reservation.bookingId).isEqualTo(expectedBookingId);
    }


    String fromFile(final String filename) {
        try {
            final URI uri = getClass().getResource(filename).toURI();
            return new String(Files.readAllBytes(Paths.get(uri)), StandardCharsets.UTF_8);
        } catch (final URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
