package train;

import helper.TestingFileUtils;
import org.junit.jupiter.api.Test;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TrainDataParserTest implements TestingFileUtils {

    @Test
    public void parseTrainData() {

        // Arrange
        JsonObject json = Json.createReader(new StringReader(fromFile("express_2000_response.json"))).readObject();

        // Act
        TrainDataParser parser = new TrainDataParser();
        TrainData trainData = parser.parse(json);

        List<Seat> seats = trainData.getSeats();

        // Assert
        assertNotNull(trainData);
        assertThat(seats).hasSize(2);

        assertThat(seats).anyMatch(s -> s.seatNumber == 1 && s.coach.equals("A") && "".equals(s.bookingReference));
        assertThat(seats).anyMatch(s -> s.seatNumber == 2 && s.coach.equals("A") && "".equals(s.bookingReference));
    }


}