package com.github.vrcca.goos.domain;

import java.util.EventListener;

public interface AuctionEventListener extends EventListener {
    void auctionClosed();

    void currentPrice(int price, int increment, PriceSource priceSource);

    enum PriceSource {
        FromSniper, FromOtherBidder;
    }
}
