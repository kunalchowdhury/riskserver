package com.quantlogic.engine;

import com.google.common.collect.Maps;
import com.quantlogic.common.entity.CacheKey;
import com.quantlogic.common.entity.SpotPrice;
import com.quantlogic.common.entity.TimedBlackVarianceVolatility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import sun.misc.Unsafe;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
public class ValuationParameterRepository {
    private static Unsafe unsafe;
    private static final ByteOrder byteOrder;
    private final Map<String, MappedByteBuffer> mbfMap;


    public void init(String mappedFile, long sz, String cacheId) {
        RandomAccessFile cacheFile;
        try {
            cacheFile = new RandomAccessFile(mappedFile, "rw");
            MappedByteBuffer byteBuffer = cacheFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, sz);
            this.mbfMap.put(cacheId, byteBuffer);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public enum ParameterType{
        SPOT, VOL, YIELDCURVE
    }
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
    static int getInt(long a) {
        boolean nativeByteOrder = (byteOrder == ByteOrder.BIG_ENDIAN);
        if (unaligned) {
            int x = unsafe.getInt(a );
            return (nativeByteOrder ? x : swap(x));
        }
        throw new RuntimeException("I DIDNT TAKE CARE OF THIS !");
    }
    private static int swap(int x) {
        return Integer.reverseBytes(x);
    }
    private final Map<String, Map<String, Integer>> spotCache;
    private final Map<String, Map<String, Integer>> volCache;
    private final Map<String, Map<String, Integer>> yieldCache;
    private final RedisTemplate<String, SpotPrice> spotPriceRedisTemplate;
    private final RedisTemplate<String, TimedBlackVarianceVolatility> blackVarianceVolatilityRedisTemplate;
    private final Map<String, Map<Integer, ParameterType>> paramType;
    private final ThreadLocal<CacheKey> cacheKeyThreadLocal = ThreadLocal.withInitial(CacheKey::new);
    private final static Logger LOGGER = LoggerFactory.getLogger(ValuationParameterRepository.class);

    public ValuationParameterRepository(@Autowired RedisTemplate<String, SpotPrice> spotPriceRedisTemplate,
                                        @Autowired RedisTemplate<String, TimedBlackVarianceVolatility> blackVarianceVolatilityRedisTemplate) {
        this.spotPriceRedisTemplate = spotPriceRedisTemplate;
        this.blackVarianceVolatilityRedisTemplate = blackVarianceVolatilityRedisTemplate;
        this.spotCache = Maps.newHashMap();
        this.volCache = Maps.newHashMap();
        this.yieldCache = Maps.newHashMap();
        this.paramType = Maps.newHashMap();
        this.mbfMap = Maps.newHashMap();
    }

    public void setSpotAddress(String cacheId, String key, long address){
        this.spotCache.computeIfAbsent(cacheId, s -> new HashMap<>()).put(key, (int) address);
    }

    public void setVolAddress(String cacheId, String key, long address){
        this.volCache.computeIfAbsent(cacheId, s -> new HashMap<>()).put(key, (int) address);
    }

    public void setYieldCacheAddress(String cacheId, String key, long address){
        this.yieldCache.computeIfAbsent(cacheId, s -> new HashMap<>()).put(key, (int) address);
    }

    public void setParameterType(String cacheId, int idx, ParameterType parameterType){
        this.paramType.computeIfAbsent(cacheId, s -> new HashMap<>()).put(idx, parameterType);
    }

    public ParameterType getParameterType(String cacheId, int idx){
        return this.paramType.get(cacheId).get(idx);
    }

    public Optional<Integer> getSpotAddress(String cacheId, String spot){
        try {
            return Optional.of(spotCache.get(cacheId).get(spot));
        }catch (Throwable t){
            LOGGER.error("{} :: {}", cacheId, spotCache);
            t.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Integer> getVolAddress(String cacheId, String vol){
        try {
            return Optional.of(volCache.get(cacheId).get(vol));
        }catch (Throwable t){
            LOGGER.error("{} :: {}", cacheId, vol);
            t.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Integer> getYieldCurveAddress(String cacheId, String ycKey){
        return Optional.of(yieldCache.get(cacheId).get(ycKey));
    }

    public Optional<SpotPrice> getSpot(String cacheId, String key, String version){
        if(version != null){
            CacheKey cacheKey = cacheKeyThreadLocal.get();
            cacheKey.setName(key);
            cacheKey.setVersion(Integer.parseInt(version));
            SpotPrice spot = (SpotPrice) spotPriceRedisTemplate.opsForHash().get("SPOTS", cacheKey);
            return Optional.of(Objects.requireNonNull(spot));
        }
        Integer index;
        if((index = spotCache.get(cacheId).get(key)) != null) {
            CacheKey cacheKey = cacheKeyThreadLocal.get();
            cacheKey.setName(key);
            int ver = mbfMap.get(cacheId).getInt(index);
            cacheKey.setVersion(ver);
            SpotPrice spot = (SpotPrice) spotPriceRedisTemplate.opsForHash().get("SPOTS", cacheKey);
            LOGGER.info("**********    Extracting value from index {}, spot {} , ver {}************** ", index , spot, ver);
            return Optional.of(Objects.requireNonNull(spot));
        }
        return Optional.empty();
    }

    public Optional<TimedBlackVarianceVolatility> getVol(String cacheId, String key, String version){
        if(version != null){
            CacheKey cacheKey = cacheKeyThreadLocal.get();
            cacheKey.setName(key);
            cacheKey.setVersion(Integer.parseInt(version));
            TimedBlackVarianceVolatility volatility =
                    (TimedBlackVarianceVolatility) blackVarianceVolatilityRedisTemplate.opsForHash().get("VOLS", cacheKey);
            return Optional.of(Objects.requireNonNull(volatility));
        }
        Integer index;
        if((index = volCache.get(cacheId).get(key)) != null) {
            CacheKey cacheKey = cacheKeyThreadLocal.get();
            cacheKey.setName(key);
            cacheKey.setVersion(mbfMap.get(cacheId).getInt(index));
            TimedBlackVarianceVolatility volatility = (TimedBlackVarianceVolatility)
                    blackVarianceVolatilityRedisTemplate.opsForHash().get("VOLS", cacheKey);
            return Optional.of(Objects.requireNonNull(volatility));
        }
        return Optional.empty();
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Hello");
        //System.out.println(unsafe.getInt(139796429500576L));
        RandomAccessFile cacheFile = new RandomAccessFile("/home/kunal/28317_HOMEMappedCache/version_cache_28317_HOME_NEW.dat", "rw");
        MappedByteBuffer mbf = cacheFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, 8);
        System.out.println(mbf.getInt(0));

        /* long l = unsafe.allocateMemory(8);
        unsafe.putInt(l , 10);
        System.out.println(l);*/
    }

}
