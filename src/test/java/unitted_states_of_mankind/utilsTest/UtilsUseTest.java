package unitted_states_of_mankind.utilsTest;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.UtilsUse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class UtilsUseTest {
    private final static double delta = 0.0000000001;
    @Test
    public void countPercentTest(){
        double expected = 200 * 10 / 100;
        Assert.assertEquals(expected, UtilsUse.countPercents(200, 10),delta );
    }

    @Test
    public void modeTest(){
        //3, 4, 6, 7, 3, 5, 3, 4)
        List<Integer> numbers = new ArrayList<>(Arrays.asList(3, 4, 6, 7, 3, 5, 3, 4));
        int expected = 3;
        int result = UtilsUse.mode(numbers);
        Assert.assertEquals(expected, result);

    }

    @Test
    public void differentPercentTest(){
        //(a/b-1)*100
        double expected = (50.0 / 100.0 - 1) * 100;
        Assert.assertEquals(expected, UtilsUse.percentDifferent(50.0, 100.0), delta);
    }

    @Test
    public void hashComplexityTest(){
        String str = "000fsgskg0";

        Assert.assertTrue(UtilsUse.hashComplexity(str, 3));
    }

    @Test
    public void notValidHashCoplexityTest(){
        String str = "s000sfs";
        Assert.assertTrue(!UtilsUse.hashComplexity(str, 3));
    }

    @Test
    public void notValidHashComplexityWrongCountTest(){
        String str = "002r2";
        Assert.assertTrue(!UtilsUse.hashComplexity(str, 3));
    }

    @Test
    public void hashCharsCountTest(){
        String var = "0fsfafdfad";//1
        String var2 = "fsf00sfsfds";//0
        String var3 = "0dsf0fsddf";//1
        String var4 = "00000fsdfsdfsf";//5
        int expected = 1;
        long result = UtilsUse.hashCount(var);
        Assert.assertEquals(expected, result);
        expected = 0;
        result = UtilsUse.hashCount(var2);
        Assert.assertEquals(expected, result);
        expected = 1;
        result = UtilsUse.hashCount(var3);
        Assert.assertEquals(expected, result);
        expected = 5;
        result = UtilsUse.hashCount(var4);
        Assert.assertEquals(expected, result);
    }


    @Test
    public void percentageShareTest(){
        int first = 50;
        int second = 200;
        double result = UtilsUse.percentageShare(first, second);
        double expected = 25.0;
        Assert.assertEquals(expected, result, delta);
    }

    @Test
    public void nearestDateToYearTest(){
        long year = (long) (Seting.COUNT_BLOCK_IN_DAY * Seting.YEAR);
        long expected = year;
        year ++;
        Assert.assertEquals(expected, UtilsUse.nearestDateToYear(year));
    }
}
