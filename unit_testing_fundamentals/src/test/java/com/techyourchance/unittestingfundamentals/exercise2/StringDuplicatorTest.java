package com.techyourchance.unittestingfundamentals.exercise2;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


public class StringDuplicatorTest {

    StringDuplicator SUT;

    @Before
    public void setup() throws Exception{
        SUT = new StringDuplicator();
    }

    @Test
    public void emptyStringOnEmptyInput(){
        String retult = SUT.duplicate("");
        assertThat(retult,is(""));
    }

    @Test
    public void singleCharacterStringOnSingleCharacterInput(){
        String retult = SUT.duplicate("a");
        assertThat(retult,is("aa"));
    }

    @Test
    public void multiCharacterStringOnMultiCharacterInput(){
        String retult = SUT.duplicate("abc");
        assertThat(retult,is("abcabc"));
    }

}