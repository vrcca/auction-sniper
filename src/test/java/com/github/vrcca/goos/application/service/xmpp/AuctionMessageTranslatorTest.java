package com.github.vrcca.goos.application.service.xmpp;

import com.github.vrcca.goos.domain.AuctionEventListener;
import com.github.vrcca.goos.domain.AuctionEventListener.PriceSource;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static _support.application.view.ApplicationRunner.SNIPER_ID;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class AuctionMessageTranslatorTest {

    public static final Chat UNUSED_CHAT = null;
    private AuctionMessageTranslator translator;

    @Mock
    private AuctionEventListener listener;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        translator = new AuctionMessageTranslator(SNIPER_ID, listener);
    }

    @Test
    public void notifiesAuctionClosedWhenCloseMessageReceived() throws Exception {
        // given
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: CLOSE;");

        // when
        translator.processMessage(UNUSED_CHAT, message);

        //then
        verify(listener).auctionClosed();
    }

    @Test
    public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromOtherBidder() throws Exception {
        // given
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: PRICE; " +
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
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: PRICE; " +
                "CurrentPrice: 234; " +
                "Increment: 5; " +
                "Bidder: " + SNIPER_ID +
                ";");

        // when
        translator.processMessage(UNUSED_CHAT, message);

        //then
        verify(listener).currentPrice(234, 5, PriceSource.FromSniper);
    }
}