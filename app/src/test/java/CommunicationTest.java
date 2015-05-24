import com.marcosdiez.spectrumanalyzer.text.AsciiAndSmallAscii;
import com.marcosdiez.spectrumanalyzer.audio.Communication;
import com.marcosdiez.spectrumanalyzer.audio.Interpreter;
import com.marcosdiez.spectrumanalyzer.text.SmallAsciiAndFrequencies;
import com.marcosdiez.spectrumanalyzer.Globals;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CommunicationTest {

    @Test
    public void testFixString() throws Exception {
        assertEquals("", Communication.fixString(""));
        assertEquals("the book is on the table", Communication.fixString("The BOOK is On the TablE"));
    }

//    @Test
//    public void testPlayerHelper(){
//        // this does not test anything but it's nice to see the results
//        // since I don't know yet how many words I will have, verifying the values make no sense
//        TestBeeper t = new TestBeeper();
//        System.out.println("-------------------A-");
//        Communication.playerHelper("123", t);
//        System.out.println("-------------------B-");
//    }

    @Test
    public void testTranslation() {
        for (int i = 0; i < AsciiAndSmallAscii.alphabet.length(); i++) {
            char c = AsciiAndSmallAscii.alphabet.charAt(i);
            String newLetter = AsciiAndSmallAscii.toSmallAscii(c);
            // System.out.println("----" + c + " -- " + newLetter + " -- ");
            char newC = AsciiAndSmallAscii.toAscii(newLetter);
            assertEquals(c, newC);
        }
    }

    @Test
    public void testPlayer() {
        // this does not test anything but it's nice to see the results
        // since I don't know yet how many words I will have, verifying the values make no sense
        // to see the output,
        // gradle testDebug -i

        String testString = "the book is on the table 2.247z;";
        TestBeeper myTestBeeper = new TestBeeper();
        System.out.println("-------------------A-");
        Communication.player(testString, myTestBeeper);
        System.out.println("-------------------B-");
    }

    @Test
    public void testInterpreter() {
        String testString = "the book is on the table 2.247z;";
        Interpreter beeper = new Interpreter();
        Communication.player(testString, beeper);
        String resultString = beeper.getOutput();
        assertEquals(testString, resultString);
    }

    @Test
    public void testSmallAsciiAndFrequencies() {
        for (int i = 0; i <= Globals.words; i++) {
            char theChar = (i + "").charAt(0);
            int convertedFrequency = SmallAsciiAndFrequencies.toFrequency(theChar);
            char convertedChar = SmallAsciiAndFrequencies.toSmallAscii(convertedFrequency);
            System.out.println(convertedChar + " -> " + convertedFrequency + " Hz");
            assertEquals(theChar, convertedChar);
        }
    }

    @Test
    public void testAsciiAndSmallAscii() {
        for (int i = 0; i < AsciiAndSmallAscii.alphabet.length(); i++) {
            char c = AsciiAndSmallAscii.alphabet.charAt(i);

            String smallAscii = AsciiAndSmallAscii.toSmallAscii(c);
            char newC = AsciiAndSmallAscii.toAscii(smallAscii);

            assertEquals(c, newC);
        }
    }

    public class TestBeeper implements Communication.Beeper {
        public void processFrequency(int frequency) {
            char c = SmallAsciiAndFrequencies.toSmallAscii(frequency);
            System.out.print(c);
        }

    }


}
