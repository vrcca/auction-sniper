package com.github.vrcca.goos.application.infrastructure;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class LoggingXMPPFailureReporterTest {

    private LoggingXMPPFailureReporter reporter;

    @Mock
    private Logger logger;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        reporter = new LoggingXMPPFailureReporter(logger);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        LogManager.getLogManager().reset();
    }

    @Test
    public void writesMessageTranslationFailureToLog() {
        // when
        reporter.cannotTranslateMessage(" auction id", "bad message", new Exception(" bad"));

        // then
        verify(logger).severe(" < auction id > " + "Could not translate message \" bad message\" " +
                "because \" java.lang.Exception:  bad\"");
    }

}