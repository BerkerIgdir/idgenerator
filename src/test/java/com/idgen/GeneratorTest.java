package com.idgen;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

@ExtendWith(SpringExtension.class)
public class GeneratorTest {
    private static final int TIMESTAMP_BITS = 41;
    private static final int RANDOM_NUMBER_BITS = 12;
    private static final int NODE_ID_BITS = 10;
    private static final int MAX_NODE_ID = (int) (Math.pow(2, NODE_ID_BITS) - 1);
    private static final int MAX_RANDOM_NUMBER = (int) (Math.pow(2, RANDOM_NUMBER_BITS) - 1);
    // Custom Epoch (January 1, 2015 Midnight UTC = 2015-01-01T00:00:00Z)
    private static final long CUSTOM_EPOCH = 1420070400000L;

    @RepeatedTest(1000)
    void generatorTest(){
        var currentTimestamp = Instant.now().toEpochMilli() - CUSTOM_EPOCH;
        System.out.println(currentTimestamp);
        if(currentTimestamp < -1){
            throw new RuntimeException("System clock error!");
        }

        var result = currentTimestamp << 22;

        var randomNumber = ThreadLocalRandom.current().nextLong() & MAX_RANDOM_NUMBER;
        System.out.println(randomNumber);
        result |= (randomNumber);
        System.out.println(result);
        Assertions.assertTrue(result >= 0);
    }

    @Test
    void macIdGetTest(){

        InetAddress ip;
        try {

            ip = InetAddress.getLocalHost();
            System.out.println("Current IP address : " + ip.getHostAddress());

            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            byte[] mac = network.getHardwareAddress();

            System.out.print("Current MAC address : ");

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            System.out.println(sb.toString());

        } catch (UnknownHostException | SocketException e) {

            e.printStackTrace();

        }


    }
}
