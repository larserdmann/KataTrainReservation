package train;

import javax.json.JsonObject;
import java.util.ArrayList;
import java.util.List;

public class TrainDataParser {

    public TrainData parse(JsonObject json) {

        List<Seat> seats = new ArrayList<>();

        JsonObject seatsJson = json.getJsonObject("seats");

        for (String seatKey : seatsJson.keySet()) {
            JsonObject seatJson = seatsJson.getJsonObject(seatKey);

            String coach = seatJson.getString("coach");
            int seatNumber = Integer.parseInt(seatJson.getString("seat_number"));
            String bookingReference = trimToNull(seatJson.getString("booking_reference"));

            seats.add(new Seat(coach, seatNumber, bookingReference));
        }

        return new TrainData(seats);
    }

    private String trimToNull(final String reference) {
        return reference != null && reference.isEmpty() ? null : reference;
    }

}
