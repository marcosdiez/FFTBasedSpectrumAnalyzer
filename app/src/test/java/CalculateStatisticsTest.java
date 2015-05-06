import com.marcosdiez.spectrumanalyzer.Globals;
import com.marcosdiez.spectrumanalyzer.audio.CalculateStatistics;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CalculateStatisticsTest {

    @Test
    public void testNormalizeIndex() throws Exception {
        assertEquals(0, CalculateStatistics.normalizeIndex(0));
        assertEquals(1000, CalculateStatistics.normalizeIndex(1000));
        assertEquals(0, CalculateStatistics.normalizeIndex(100));
        assertEquals(Globals.max_frequency, CalculateStatistics.normalizeIndex(333333));
        assertEquals(Globals.max_frequency, Globals.max_frequency);
        assertEquals(500, CalculateStatistics.normalizeIndex(400));
        assertEquals(500, CalculateStatistics.normalizeIndex(298));
        assertEquals(2000, CalculateStatistics.normalizeIndex(2222));
        assertEquals(3000, CalculateStatistics.normalizeIndex(2800));
        assertEquals(2500, CalculateStatistics.normalizeIndex(2700));
        assertEquals(1500, CalculateStatistics.normalizeIndex(1456));
        assertEquals(2500, CalculateStatistics.normalizeIndex(2345));
        assertEquals(3000, CalculateStatistics.normalizeIndex(3100));
        assertEquals(3000, CalculateStatistics.normalizeIndex(3200));
        assertEquals(3000, CalculateStatistics.normalizeIndex(3300));
        assertEquals(3000, CalculateStatistics.normalizeIndex(3400));
    }

}