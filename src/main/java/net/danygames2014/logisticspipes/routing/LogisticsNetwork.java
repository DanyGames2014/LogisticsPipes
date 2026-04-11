package net.danygames2014.logisticspipes.routing;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class LogisticsNetwork {
    public ObjectOpenHashSet<Router> routers;

    public LogisticsNetwork() {
        this(new ObjectOpenHashSet<>());
    }

    public LogisticsNetwork(ObjectOpenHashSet<Router> routers) {
        this.routers = routers;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void finalize() throws Throwable {
        System.err.println("LogisticsNetwork Finalized");
        super.finalize();
    }

    @Override
    public String toString() {
        return "LogisticsNetwork{" +
                "routers=" + routers +
                '}';
    }
}
