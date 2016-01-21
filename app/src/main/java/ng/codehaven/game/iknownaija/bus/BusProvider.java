package ng.codehaven.game.iknownaija.bus;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by Thompson on 30/10/2015.
 */
public final class BusProvider {
    private static final Bus BUS = new Bus(ThreadEnforcer.ANY);
    public static Bus getInstance(){
        return BUS;
    }
}
