package com.idgen.generator;


import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class IDGenerator {

    private static final int TIMESTAMP_BITS = 41;
    private static final int RANDOM_NUMBER_BITS = 12;
    private static final int NODE_ID_BITS = 10;
    private static final int MAX_NODE_ID = (int) (Math.pow(2, NODE_ID_BITS) - 1);
    private static final int MAX_RANDOM_NUMBER = (int) (Math.pow(2, RANDOM_NUMBER_BITS) - 1);
    private volatile long lastTimestamp = -1L;

    // Custom Epoch (January 1, 2015 Midnight UTC = 2015-01-01T00:00:00Z)
    // 41 BIT
    private static final long CUSTOM_EPOCH = 1420070400000L;

    public synchronized long generateId(){
        var currentTimestamp = currentTimestamp();

        if(currentTimestamp < lastTimestamp){
            throw new RuntimeException("System clock error!");
        }

        lastTimestamp = currentTimestamp;

        var randomAbsNumber = ThreadLocalRandom.current().nextLong() & MAX_RANDOM_NUMBER;

        var result = currentTimestamp << RANDOM_NUMBER_BITS;
        result |= (getMacId().orElseThrow(RuntimeException::new) << RANDOM_NUMBER_BITS);
        result |= randomAbsNumber;
        return result;
    }

    private Optional<Long> getMacId(){
        InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();

            var network = NetworkInterface.getByInetAddress(ip);

            byte[] mac = network.getHardwareAddress();

            var sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            return Optional.of((long) sb.toString().hashCode() & MAX_NODE_ID);

        } catch (UnknownHostException | SocketException e) {

            e.printStackTrace();
            return Optional.empty();
        }
    }

    private long currentTimestamp(){
        return Instant.now().toEpochMilli() - CUSTOM_EPOCH;
    }
}
