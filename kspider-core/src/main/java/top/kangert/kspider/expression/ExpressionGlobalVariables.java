package top.kangert.kspider.expression;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ExpressionGlobalVariables {

    private static Map<String, String> variables = new HashMap<>();

    private static ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public static void reset(Map<String, String> map) {
        Lock lock = readWriteLock.writeLock();
        lock.lock();
        try {
            variables.clear();
            variables.putAll(map);
        } finally {
            lock.unlock();
        }
    }

    public static void update(String key, String value) {
        Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try {
            Lock readLock = readWriteLock.readLock();
            readLock.lock();
            try {
                if (variables.containsKey(key)) {
                    variables.put(key, value);
                }
            } finally {
                readLock.unlock();
            }
        } finally {
            writeLock.unlock();
        }
    }

    public static Map<String, String> getVariables() {
        Lock lock = readWriteLock.readLock();
        lock.lock();
        try {
            return variables;
        } finally {
            lock.unlock();
        }
    }
}
