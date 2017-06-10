package com.github.vrcca.goos.application.infrastructure;

import com.github.vrcca.goos.application.service.xmpp.XMPPFailureReporter;

import java.util.logging.Logger;

public class LoggingXMPPFailureReporter implements XMPPFailureReporter {

    private final Logger logger;

    public LoggingXMPPFailureReporter(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void cannotTranslateMessage(String auctionId, String failedMessage, Exception exception) {
        logger.severe(" < auction id > " + "Could not translate message \" " + failedMessage + "\" " +
                "because \" " + exception + "\"");
    }
}
