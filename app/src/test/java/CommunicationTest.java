import com.marcosdiez.spectrumanalyzer.audio.Communication;

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
    public void testTranslation(){
        for(int i = 0 ; i < Communication.alphabet.length(); i++ ){
            char c = Communication.alphabet.charAt(i);
            String newLetter = Communication.toNewBase(c);
            // System.out.println("----" + c + " -- " + newLetter + " -- ");
            char newC = Communication.toLetter(newLetter);
            assertEquals(c, newC);
        }
    }

    @Test
    public void testPlayer(){
        // this does not test anything but it's nice to see the results
        // since I don't know yet how many words I will have, verifying the values make no sense
        // to see the output,
        // gradle testDebug -i

        String testString = "the book is on the table 22z.";
        TestBeeper t = new TestBeeper();
        System.out.println("-------------------A-");
        Communication.player(testString, t);
        System.out.println("-------------------B-");
    }

    public class TestBeeper implements Communication.Beeper {
        public void beepChar(char c) {
            System.out.print(c);
        }

        public void beepWordSeparator() {
            System.out.println("!");
        }
    }

}
