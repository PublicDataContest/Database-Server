package com.example.publicdataserver.repository.test;

import com.example.publicdataserver.domain.PublicData;
import com.example.publicdataserver.repository.PublicDataRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class PublicDataRepositoryTest {
    @Autowired
    private PublicDataRepository publicDataRepository;

    @Test
    @DisplayName("데이터 등록")
    public void 데이터_등록() {
        // given
        final PublicData publicData = PublicData.builder()
                .title("제목")
                .deptNm("부서명")
                .url("문서url")
                .execDt("집행일시")
                .execLoc("집행장소")
                .execPurpose("집행목적")
                .execAmount("집행금액")
                .build();

        // when
        final PublicData result = publicDataRepository.save(publicData);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getTitle()).isEqualTo("제목");
        assertThat(result.getDeptNm()).isEqualTo("부서명");
        assertThat(result.getUrl()).isEqualTo("문서url");
        assertThat(result.getExecDt()).isEqualTo("집행일시");
        assertThat(result.getExecLoc()).isEqualTo("집행장소");
        assertThat(result.getExecPurpose()).isEqualTo("집행목적");
        assertThat(result.getExecAmount()).isEqualTo("집행금액");
    }

    @Test
    @DisplayName("데이터_존재하는지_확인")
    public void 데이터_존재하는지_확인() {
        // given
        final PublicData publicData = PublicData.builder()
                .title("제목")
                .deptNm("부서명")
                .url("문서url")
                .execDt("집행일시")
                .execLoc("집행장소")
                .execPurpose("집행목적")
                .execAmount("집행금액")
                .build();

        // when
        PublicData findResult = publicDataRepository.save(publicData);

        // then
        assertThat(findResult.getId()).isNotNull();
        assertThat(findResult.getTitle()).isEqualTo("제목");
        assertThat(findResult.getDeptNm()).isEqualTo("부서명");
        assertThat(findResult.getUrl()).isEqualTo("문서url");
        assertThat(findResult.getExecDt()).isEqualTo("집행일시");
        assertThat(findResult.getExecLoc()).isEqualTo("집행장소");
        assertThat(findResult.getExecPurpose()).isEqualTo("집행목적");
        assertThat(findResult.getExecAmount()).isEqualTo("집행금액");
    }
}
