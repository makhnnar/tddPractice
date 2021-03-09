package com.techyourchance.unittestingfundamentals.exercise1;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

public class NegativeNumberValidatorTest {

    NegativeNumberValidator SUT;

    @Before
    public void setup(){
        SUT = new NegativeNumberValidator();
    }

    @Test
    public void isNotNegativeWithPositiveValue(){
        boolean result = SUT.isNegative(1);
        Assert.assertThat(result,is(false));
    }

    @Test
    public void isNotNegativeWithCeroValue(){
        boolean result = SUT.isNegative(0);
        Assert.assertThat(result,is(false));
    }

    @Test
    public void isNegativeWithNegativeValue(){
        boolean result = SUT.isNegative(-1);
        Assert.assertThat(result,is(true));
    }

}