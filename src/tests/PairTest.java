package tests;

import com.company.server.Pair;
import com.example.customchess.engine.misc.Team;
import com.example.customchess.networking.ConnectionPacket;
import com.example.customchess.networking.ConnectionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PairTest {
    private Pair pair;

    public void setUp() {
        pair = new Pair(new ClientForTestReasons(
                new ConnectionPacket(Team.White, 55, ConnectionType.CONNECT)));
    }

    @Test
    public void test1() {
        setUp();
        ClientForTestReasons client = new ClientForTestReasons(
                new ConnectionPacket(Team.Black, 55, ConnectionType.CONNECT)
        );
        client.disable();
        pair.addClient(client);
        assertFalse(pair.isReadyForGame());
    }

    @Test
    public void test2() {
        setUp();
        ClientForTestReasons client = new ClientForTestReasons(
                new ConnectionPacket(Team.Black, 55, ConnectionType.CONNECT)
        );
        pair.addClient(client);
        assertTrue(pair.isReadyForGame());
    }

    @Test
    public void test3() {
        ClientForTestReasons first = new ClientForTestReasons(
                new ConnectionPacket(Team.White, 55, ConnectionType.CONNECT)
        );
        first.disable();
        ClientForTestReasons second = new ClientForTestReasons(
                new ConnectionPacket(Team.Black, 55, ConnectionType.CONNECT)
        );
        ClientForTestReasons third = new ClientForTestReasons(
                new ConnectionPacket(Team.White, 55, ConnectionType.CONNECT)
        );
        pair = new Pair(second);
        pair.addClient(first);
        pair.removeInactiveClients();
        pair.addClient(third);
        boolean res = pair.isReadyForGame();
        assertTrue(res);
    }

    @Test
    public void test4() {
        ClientForTestReasons inactiveFirst = new ClientForTestReasons(
                new ConnectionPacket(Team.White, 55, ConnectionType.CONNECT)
        ); inactiveFirst.disable();
        pair = new Pair(inactiveFirst);
        ClientForTestReasons activeSecond = new ClientForTestReasons(
                new ConnectionPacket(Team.Black, 55, ConnectionType.CONNECT)
        );
        pair.addClient(activeSecond);
        assertFalse(pair.isReadyForGame());
    }

    @Test
    public void test5() {
        ClientForTestReasons inactiveFirst = new ClientForTestReasons(
                new ConnectionPacket(Team.White, 55, ConnectionType.CONNECT)
        ); inactiveFirst.disable();
        pair = new Pair(inactiveFirst);
        ClientForTestReasons inactiveSecond = new ClientForTestReasons(
                new ConnectionPacket(Team.Black, 55, ConnectionType.CONNECT)
        ); inactiveSecond.disable();
        pair.addClient(inactiveSecond);
        pair.removeInactiveClients();
        assertFalse(pair.isReadyForGame());
    }
}