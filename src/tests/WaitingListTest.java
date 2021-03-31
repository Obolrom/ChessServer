package tests;

import com.company.server.Client;
import com.company.server.Pair;
import com.company.server.WaitingList;
import com.example.customchess.engine.misc.Team;
import com.example.customchess.networking.ConnectionPacket;
import com.example.customchess.networking.ConnectionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class WaitingListTest {
    private WaitingList waitingList;

    @BeforeEach
    public void setUp() {
        waitingList = new WaitingList();
    }

    @Test
    public void test1() {
        waitingList.push(new ClientForTestReasons(new ConnectionPacket(
                Team.White, 55, ConnectionType.CONNECT
        )));
        waitingList.push(new ClientForTestReasons(new ConnectionPacket(
                Team.Black, 55, ConnectionType.CONNECT
        )));
        assertTrue(waitingList.isAnyReadyPair());
    }

    @Test
    public void test2() {
        waitingList.push(new ClientForTestReasons(new ConnectionPacket(
                Team.White, 55, ConnectionType.CONNECT
        )));
        waitingList.push(new ClientForTestReasons(new ConnectionPacket(
                Team.White, 55, ConnectionType.CONNECT
        )));
        waitingList.push(new ClientForTestReasons(new ConnectionPacket(
                Team.Black, 10, ConnectionType.CONNECT
        )));
        assertFalse(waitingList.isAnyReadyPair());
    }

    @Test
    public void test3() {
        waitingList.push(new ClientForTestReasons(new ConnectionPacket(
                Team.White, 55, ConnectionType.CONNECT
        )));
        waitingList.push(new ClientForTestReasons(new ConnectionPacket(
                Team.White, 55, ConnectionType.CONNECT
        )));
        waitingList.push(new ClientForTestReasons(new ConnectionPacket(
                Team.Black, 10, ConnectionType.CONNECT
        )));
        assertFalse(waitingList.isAnyReadyPair());
    }

    @Test
    public void test4() {
        ClientForTestReasons client1 = new ClientForTestReasons(new ConnectionPacket(
                Team.White, 55, ConnectionType.CONNECT
        )); client1.disable();
        waitingList.push(client1);
        ClientForTestReasons client2 = new ClientForTestReasons(new ConnectionPacket(
                Team.Black, 55, ConnectionType.CONNECT
        )); client2.disable();
        waitingList.push(new ClientForTestReasons(new ConnectionPacket(
                Team.White, 15, ConnectionType.CONNECT
        )));
        waitingList.push(new ClientForTestReasons(new ConnectionPacket(
                Team.Black, 10, ConnectionType.CONNECT
        )));
        assertEquals(0, waitingList.getReadyPairsForGame().size());
    }

    @Test
    public void test5() {
        waitingList.push(new ClientForTestReasons(new ConnectionPacket(
                Team.White, 15, ConnectionType.CONNECT
        )));
        waitingList.push(new ClientForTestReasons(new ConnectionPacket(
                Team.Black, 15, ConnectionType.CONNECT
        )));
        assertEquals(1, waitingList.getReadyPairsForGame().size());
    }
}
