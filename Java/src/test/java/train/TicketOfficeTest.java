package train;

import com.github.jenspiegsa.wiremockextension.Managed;
import com.github.jenspiegsa.wiremockextension.WireMockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;
import helper.TestingFileUtils;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.github.jenspiegsa.wiremockextension.ManagedWireMockServer.with;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("WireMockExtension usage")
@ExtendWith(WireMockExtension.class)
@ExtendWith(MockitoExtension.class)
public class TicketOfficeTest implements TestingFileUtils {

    @Managed
    WireMockServer bookingRefServer = with(wireMockConfig().dynamicPort());
    @Managed
    WireMockServer trainDataServer = with(wireMockConfig().dynamicPort());
    @Mock
    TrainDataParser trainDataParser;
    @Mock
    TrainData trainData;
    @Mock
    ReservationService reservationService;

    private String bookingRefBaseUrl;
    private String trainDataBaseUrl;

    @Test
    public void reserveSeats() {

        String expectedBookingId = "75bcd15";
        String json = fromFile("express_2000_response.json");

        bookingRefServer.givenThat(get("/booking_reference").willReturn(ok(expectedBookingId)));
        trainDataServer.givenThat(get("/data_for_train/express_2000").willReturn(okJson(json)));

        given(trainDataParser.parse(any())).willReturn(trainData);

        bookingRefBaseUrl = bookingRefServer.baseUrl();
        trainDataBaseUrl = trainDataServer.baseUrl();

        ReservationRequest request = new ReservationRequest("express_2000", 123);
        TicketOffice ticketOffice = new TicketOffice(bookingRefBaseUrl, trainDataBaseUrl, trainDataParser, reservationService);


        Reservation reservation = ticketOffice.makeReservation(request);


        BDDAssertions.then(reservation).isNotNull();
        BDDAssertions.then(reservation.bookingId).isEqualTo(expectedBookingId);
    }

}
