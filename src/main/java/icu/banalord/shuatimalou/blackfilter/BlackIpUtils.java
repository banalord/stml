package icu.banalord.shuatimalou.blackfilter;

import cn.hutool.bloomfilter.BitMapBloomFilter;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import org.yaml.snakeyaml.Yaml;

import java.util.List;
import java.util.Map;

/**
 * 黑名单过滤工具类
 *
 */
public class BlackIpUtils {
    private static BitMapBloomFilter bloomFilter = new BitMapBloomFilter(100);

    // 判断ip是否在黑名单内
    public static boolean isBlackIP(String ip) {
        return bloomFilter.contains(ip);
    }

    /**
     * 重建 ip 黑名单
     * @param configInfo
     */
    public static void rebuildBlackIP(String configInfo) {
        if (StrUtil.isBlank(configInfo)) {
            configInfo = "{}";
        }
        // 解析 yaml 文件
        Yaml yaml = new Yaml();
        Map map = yaml.loadAs(configInfo, Map.class);
        // 获取 IP 黑名单
        List<String> blackIpList = (List<String>) map.get("blackIpList");
        synchronized (BlackIpUtils.class){
            if(CollUtil.isNotEmpty(blackIpList)){
                bloomFilter = new BitMapBloomFilter(958506);
                for (String blackIP : blackIpList) {
                    bloomFilter.add(blackIP);
                }
            }else{
                bloomFilter = new BitMapBloomFilter(100);
            }
        }

    }
}
