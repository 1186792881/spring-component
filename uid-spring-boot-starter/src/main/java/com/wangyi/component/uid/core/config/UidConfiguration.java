package com.wangyi.component.uid.core.config;

import com.wangyi.component.uid.core.impl.CachedUidGenerator;
import com.wangyi.component.uid.core.resposity.AssignerMode;
import com.wangyi.component.uid.core.resposity.DisposableWorkerIdAssigner;
import com.wangyi.component.uid.core.resposity.WorkerNodeResposity;
import com.wangyi.component.uid.core.resposity.impl.WorkerNodeDefault;
import com.wangyi.component.uid.core.resposity.impl.WorkerNodeRedis;
import com.wangyi.component.uid.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 配置
 *
 * @author lyt
 */
@Configuration
@ComponentScan("com.wangyi.component.uid")
@EnableConfigurationProperties(UidProperties.class)
public class UidConfiguration {

    @Autowired
    private UidProperties uidProperties;

    @Bean
    @ConditionalOnProperty(value = "uid.assigner-mode", havingValue = AssignerMode.REDIS)
    @Lazy
    public WorkerNodeResposity redisWorkerNodeResposity(StringRedisTemplate stringRedisTemplate) {
        return new WorkerNodeRedis(stringRedisTemplate, uidProperties);
    }

    @Bean
    @ConditionalOnProperty(value = "uid.assigner-mode", havingValue = AssignerMode.DEFAULT, matchIfMissing = true)
    @Lazy
    public WorkerNodeResposity defaultWorkerNodeResposity() {
        return new WorkerNodeDefault(uidProperties);
    }

    @Bean
    @Lazy
    public DisposableWorkerIdAssigner disposableWorkerIdAssigner() {
        return new DisposableWorkerIdAssigner();
    }

    @Bean
    @Lazy
    public CachedUidGenerator cachedUidGenerator(DisposableWorkerIdAssigner workerIdAssigner) {
        CachedUidGenerator generator = new CachedUidGenerator();
        generator.setWorkerIdAssigner( workerIdAssigner );

        // 以下为可选配置, 如未指定将采用默认值
        if (uidProperties.getTimeBits() > 0) {
            generator.setTimeBits( uidProperties.getTimeBits() );
        }
        if (uidProperties.getWorkerBits() > 0) {
            generator.setWorkerBits( uidProperties.getWorkerBits() );
        }
        if (uidProperties.getSeqBits() > 0) {
            generator.setSeqBits( uidProperties.getSeqBits() );
        }
        if (StringUtils.isNotEmpty( uidProperties.getEpochStr() )) {
            generator.setEpochStr( uidProperties.getEpochStr() );
        }

        // RingBuffer size扩容参数, 可提高UID生成的吞吐量
        // 默认:3， 原bufferSize=8192, 扩容后bufferSize= 8192 << 3 = 65536
        if (uidProperties.getBoostPower() > 0) {
            generator.setBoostPower( uidProperties.getBoostPower() );
        }

        // 指定何时向RingBuffer中填充UID, 取值为百分比(0, 100), 默认为50
        // 举例: bufferSize=1024, paddingFactor=50 -> threshold=1024 * 50 / 100 = 512.
        // 当环上可用UID数量 < 512时, 将自动对RingBuffer进行填充补全
        if (uidProperties.getPaddingFactor() > 0) {
            generator.setPaddingFactor( uidProperties.getPaddingFactor() );
        }

        // 另外一种RingBuffer填充时机, 在Schedule线程中, 周期性检查填充
        // 默认:不配置此项, 即不使用Schedule线程. 如需使用, 请指定Schedule线程时间间隔, 单位:秒
        if (uidProperties.getScheduleInterval() > 0) {
            generator.setScheduleInterval( uidProperties.getPaddingFactor() );
        }

        // 拒绝策略: 当环已满, 无法继续填充时
        // 默认无需指定, 将丢弃Put操作, 仅日志记录. 如有特殊需求, 请实现RejectedPutBufferHandler接口(支持Lambda表达式)
        // <property name="rejectedPutBufferHandler"
        // ref="XxxxYourPutRejectPolicy"></property>
        // cachedUidGenerator.setRejectedPutBufferHandler();
        // 拒绝策略: 当环已空, 无法继续获取时 -->
        // 默认无需指定, 将记录日志, 并抛出UidGenerateException异常. 如有特殊需求,
        // 请实现RejectedTakeBufferHandler接口(支持Lambda表达式) -->
        // <property name="rejectedTakeBufferHandler"
        // ref="XxxxYourTakeRejectPolicy"></property>

        return generator;
    }

}
