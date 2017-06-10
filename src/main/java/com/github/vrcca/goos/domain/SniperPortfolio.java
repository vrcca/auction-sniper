package com.github.vrcca.goos.domain;

import com.github.vrcca.goos.domain.utils.Announcer;

import java.util.ArrayList;
import java.util.List;

public class SniperPortfolio implements SniperCollector {

    private final Announcer<PortfolioListener> portfolioListeners = Announcer.to(PortfolioListener.class);
    private final List<AuctionSniper> notToBeGCd = new ArrayList<>();

    @Override
    public void addSniper(AuctionSniper sniper) {
        notToBeGCd.add(sniper);
        portfolioListeners.announce().sniperAdded(sniper);
    }

    public void addPortfolioListener(PortfolioListener listener) {
        portfolioListeners.addListener(listener);
    }
}
