package com.quantlogic.mmap;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessController;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class MemoryMapManagerImpl implements MemoryMapManager{
    private static final Logger LOGGER = LoggerFactory.getLogger(MemoryMapManagerImpl.class);
    private static Unsafe unsafe;
    private static final ByteOrder byteOrder;
    static {
        try {
            final Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            unsafe = (Unsafe) theUnsafe.get(null);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);

        }
    }
    private static final boolean unaligned;

    static {
        String arch = AccessController.doPrivileged(
                new sun.security.action.GetPropertyAction("os.arch"));
        unaligned = arch.equals("i386") || arch.equals("x86")
                || arch.equals("amd64") || arch.equals("x86_64");
    }

    static {
        long a = unsafe.allocateMemory(8);
        try {
            unsafe.putLong(a, 0x0102030405060708L);
            byte b = unsafe.getByte(a);
            switch (b) {
                case 0x01: byteOrder = ByteOrder.BIG_ENDIAN;     break;
                case 0x08: byteOrder = ByteOrder.LITTLE_ENDIAN;  break;
                default:
                    assert false;
                    byteOrder = null;
            }
        } finally {
            unsafe.freeMemory(a);
        }
    }
    private static final String FILE_SUFFIX = ".dat";
    private final Map<Long, Long> rootAddresses;
    private final Map<Long, Boolean> usedAddressMap;
    private final Map<String, MappedByteBuffer> mbfMap;
    private final Map<String, String> cacheFileNameMap;
    private final Map<String, Set<Integer>> usedLocations;

    public MemoryMapManagerImpl() {
        this.rootAddresses = new LinkedHashMap<>();
        this.usedAddressMap = new LinkedHashMap<>();
        this.cacheFileNameMap = Maps.newConcurrentMap();
        this.usedLocations = Maps.newConcurrentMap();
        this.mbfMap = Maps.newConcurrentMap();
    }

    @Override
    public long getStartAddress(long memoryAddress) {
        return this.rootAddresses.get(memoryAddress);
    }

    @Override
    public void set(long memoryAddress, int version) {
        unsafe.putInt(memoryAddress, version);
    }

    @Override
    public void putInBuffer(String cacheId, int index, int version) {
        mbfMap.get(cacheId).putInt(index, version);
    }

    @Override
    public String getMappedFileName(String cacheId) {
        return cacheFileNameMap.get(cacheId);
    }

    @Override
    public void markUsed(long memoryAddress) {
        usedAddressMap.put(memoryAddress, true);
    }

    @Override
    public void markUsedInBuffer(String cacheId, int index) {
        usedLocations.computeIfAbsent(cacheId, s -> Sets.newHashSet()).add(index);
    }

    @Override
    public void markFreeInBuffer(String cacheId, int index) {
        usedLocations.get(cacheId).remove(index);
    }

    @Override
    public void markFree(long memoryAddress) {
        usedAddressMap.put(memoryAddress, false);
    }

    @Override
    public boolean isFree(long memoryAddress) {
        return !usedAddressMap.get(memoryAddress);
    }

    @Override
    public boolean isFree(String cacheId, int idx) {
        return !usedLocations.get(cacheId).contains(idx);
    }

    @Override
    public Set<Long> getUsedAddresses() {
        return usedAddressMap.entrySet().stream().filter(e -> !e.getValue()).map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    public Set<Long> getAddressSet() {
        return this.rootAddresses.keySet();
    }

    @Override
    public Long reserveMemory(String cacheId, int spotsCount, int volsCount, int yieldCurveCount) {
        try {
            double sz = 4 * (spotsCount + volsCount + yieldCurveCount);
            Path path = Paths.get(System.getProperty("user.home")+ File.separator+cacheId+"MappedCache");
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
            final String fileName = path.toAbsolutePath().toString()+ File.separator +"version_cache_" + cacheId + FILE_SUFFIX;
            this.cacheFileNameMap.put(cacheId, fileName);
            RandomAccessFile cacheFile = new RandomAccessFile(fileName, "rw");
            MappedByteBuffer byteBuffer = cacheFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, (long) sz);
            this.mbfMap.put(cacheId, byteBuffer);
            this.usedLocations.put(cacheId, new HashSet<>());
            long rootAddress = ((DirectBuffer) byteBuffer).address();
            for (int i = 0; i < sz; i+=4) {
                long key = rootAddress + i;
                LOGGER.info("Root address set key {}, value {}", key, rootAddress);
                this.rootAddresses.put(key, rootAddress);
                this.usedAddressMap.put(key, false);
            }
            return (long) sz;

        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("This should not have occured for "+cacheId);
    }

    @Override
    public int getValue(long address) {
        return unsafe.getInt(address);
    }

    private static int swap(int x) {
        return Integer.reverseBytes(x);
    }
    static int getInt(long a) {
        boolean nativeByteOrder = (byteOrder == ByteOrder.BIG_ENDIAN);
        if (unaligned) {
            int x = unsafe.getInt(a );
            return (nativeByteOrder ? x : swap(x));
        }
        throw new RuntimeException("I DIDNT TAKE CARE OF THIS !");
    }

    public static void main(String[] args) {
        MemoryMapManagerImpl m = new MemoryMapManagerImpl();
        Long aapl = m.reserveMemory("AAPL", 2, 3, 1);
        m.set(aapl, 1901);
        System.out.println(m.getValue(aapl));
        m.set(aapl + 4, 1902);
        System.out.println(m.getValue(aapl + 4));

        Long fb = m.reserveMemory("FB", 7, 3, 1);
        m.set(fb, 1910);
        System.out.println(m.getValue(fb));
        m.set(fb + 4, 1920);
        m.set(fb + 8, 1930);
        System.out.println(m.getValue(fb + 4));
        System.out.println(m.getValue(fb + 8));
    }
}
