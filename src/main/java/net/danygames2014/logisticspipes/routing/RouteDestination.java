package net.danygames2014.logisticspipes.routing;

public class RouteDestination {
    public long routerId;   
    public Router router;
    public int metric;

    public RouteDestination(long routerId, Router router, int metric) {
        this.routerId = routerId;
        this.router = router;
        this.metric = metric;
    }

    @Override
    public String toString() {
        return "Route{" + "routerId=" + routerId + ", metric=" + metric + '}';
    }
}
