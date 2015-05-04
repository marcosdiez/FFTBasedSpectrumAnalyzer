import com.marcosdiez.spectrumanalyzer.util.Misc;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OtherTests {

    @Test
    public void testNormalizeIndex() throws Exception {
        long theDate = 1430772039L; // Mon, 04 May 2015 20:40:39 GMT
        String myDate = Misc.epochToDate(theDate);
        String expectedDate = "20150504204039";
        assertEquals(expectedDate, myDate);
    }
}
