package train;

import helper.TestingFileUtils;
import org.junit.jupiter.api.Test;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class TrainDataParserTest implements TestingFileUtils {

    @Test
    public void test() {

        // Arrange
        JsonObject json = Json.createReader(new StringReader(fromFile("express_2000_response.json"))).readObject();

        // Act
        TrainDataParser parser = new TrainDataParser();
        TrainData result = parser.parse(json);

        // Assert
        assertNotNull(result);

    }

}