package io.deeplay.qlab.util;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


public class CmdLineArgsTest {
    @Test
    void parseTest() {
        CmdLineArgs cmdLineArgs = CmdLineArgs.parse(new String[]{"--history=/some/path/to/anonymized_data.json"});

        assertEquals("/some/path/to/anonymized_data.json", cmdLineArgs.getHistory());
    }


    @Test
    void parseTestWithSpace() {
        CmdLineArgs cmdLineArgs = CmdLineArgs.parse(new String[]{"--history=/some/path/to/anonymized data.json"});

        assertEquals("/some/path/to/anonymized data.json", cmdLineArgs.getHistory());
    }


    @Test
    void parseTestNoHistory() {
        CmdLineArgs cmdLineArgs = CmdLineArgs.parse(new String[]{"--path=/some/path/to/data.json"});

        assertNull(cmdLineArgs.getHistory());
    }
}
