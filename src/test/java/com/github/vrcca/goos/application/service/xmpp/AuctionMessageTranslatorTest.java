package com.github.vrcca.goos.application.service.xmpp;

import com.github.vrcca.goos.domain.AuctionEventListener;
import com.github.vrcca.goos.domain.AuctionEventListener.PriceSource;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static _support.application.view.ApplicationRunner.SNIPER_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class AuctionMessageTranslatorTest {

    private static final Chat UNUSED_CHAT = null;

    private AuctionMessageTranslator translator;

    @Mock
    private XMPPFailureReporter failureReporter;

    @Mock
    private AuctionEventListener listener;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        translator = new AuctionMessageTranslator(SNIPER_ID, listener, failureReporter);
    }

    @Test
    public void notifiesAuctionClosedWhenCloseMessageReceived() throws Exception {
        // given
        Message message = message("" +
                "SOLVersion: 1.1; " +
                "Event: CLOSE;");

        // when
        translator.processMessage(UNUSED_CHAT, message);

        //then
        verify(listener).auctionClosed();
    }

    @Test
    public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromOtherBidder() throws Exception {
        // given
        Message message = message("" +
                "SOLVersion: 1.1; " +
                "Event: PRICE; " +
                "CurrentPrice: 192; " +
                "Increment: 7; " +
                "Bidder: Someone else;");

        // when
        translator.processMessage(UNUSED_CHAT, message);

        //then
        verify(listener).currentPrice(192, 7, PriceSource.FromOtherBidder);
    }

    @Test
    public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromSniper() throws Exception {
        // given
        Message message = message("" +
                "SOLVersion: 1.1; " +
                "Event: PRICE; " +
                "CurrentPrice: 234; " +
                "Increment: 5; " +
                "Bidder: " + SNIPER_ID +
                ";");

        // when
        translator.processMessage(UNUSED_CHAT, message);

        //then
        verify(listener).currentPrice(234, 5, PriceSource.FromSniper);
    }

    @Test
    public void notifiesAuctionFailedWhenBadMessageReceived() throws Exception {
        // given
        final String messageBody = "A bad message";
        Message message = message(messageBody);

        // when
        translator.processMessage(UNUSED_CHAT, message);

        //then
        verify(listener).auctionFailed();
        expectFailureWithMessage(messageBody);
    }

    private void expectFailureWithMessage(String messageBody) {
        verify(failureReporter).cannotTranslateMessage(eq(SNIPER_ID), eq(messageBody), any(Exception.class));
    }

    @Test
    public void notifiesAuctionFailedWhenEventTypeMissing() {
        // given
        final String messageBody = " " +
                "SOLVersion: 1.1; " +
                "CurrentPrice: 234; " +
                "Increment: 5; " +
                "Bidder: " + SNIPER_ID + ";";
        Message message = message(messageBody);

        // when
        translator.processMessage(UNUSED_CHAT, message);

        // then
        verify(listener).auctionFailed();
        expectFailureWithMessage(messageBody);
    }

    private Message message(String body) {
        Message message = new Message();
        message.setBody(body);
        return message;
    }
}