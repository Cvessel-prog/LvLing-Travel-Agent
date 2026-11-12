package com.guido.scenicai.module.knowledge.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guido.scenicai.integration.embedding.EmbeddingClientRouter;
import com.guido.scenicai.module.knowledge.entity.KbChunk;
import com.guido.scenicai.module.knowledge.mapper.KbChunkMapper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LocalRagServiceTest {

    @Mock private KbChunkMapper kbChunkMapper;
    @Mock private EmbeddingClientRouter embeddingClientRouter;

    private LocalRagService service;

    @BeforeAll
    static void initTableInfo() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new Configuration(), ""), KbChunk.class);
    }

    @BeforeEach
    void setUp() {
        service = new LocalRagService(kbChunkMapper, embeddingClientRouter, new ObjectMapper());
    }

    @Test
    @SuppressWarnings("unchecked")
    void retrieveAppliesCityFilterWhenCityIdPresent() {
        ArgumentCaptor<LambdaQueryWrapper<KbChunk>> captor = ArgumentCaptor.forClass(LambdaQueryWrapper.class);
        when(kbChunkMapper.selectList(captor.capture())).thenReturn(List.of());

        service.retrieve(10L, null, "灵山大佛");

        assertTrue(captor.getValue().getSqlSegment().contains("cityId"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void retrieveSkipsCityFilterWhenCityIdMissing() {
        ArgumentCaptor<LambdaQueryWrapper<KbChunk>> captor = ArgumentCaptor.forClass(LambdaQueryWrapper.class);
        when(kbChunkMapper.selectList(captor.capture())).thenReturn(List.of());

        service.retrieve(null, null, "灵山大佛");

        assertFalse(captor.getValue().getSqlSegment().contains("cityId"));
    }
}
