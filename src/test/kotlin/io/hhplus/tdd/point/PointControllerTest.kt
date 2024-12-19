package io.hhplus.tdd.point

import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(PointController::class)
class PointControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var pointService: PointService

    // 포인트 조회 API 단위 테스트
    @Test
    fun point() {
        // given
        given(pointService.getPoint(1L)).willReturn(UserPoint(1L, 1000L, 123L))

        // when then
        mockMvc.perform(
            get("/point/{id}", 1L)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("id").value(1L))
            .andExpect(jsonPath("point").value(1000L))
            .andExpect(jsonPath("updateMillis").value(123L))
    }

    // 포인트 내역 조회 API 단위 테스트
    @Test
    fun history() {
        // given
        given(pointService.getHistories(1L)).willReturn(
            listOf(
                PointHistory(1L, 1L, TransactionType.CHARGE, 2000L, 123L),
                PointHistory(2L, 1L, TransactionType.USE, 1000L, 456L),
            )
        )

        // when then
        mockMvc.perform(
            get("/point/{id}/histories", 1L)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("[0].id").value(1L))
            .andExpect(jsonPath("[0].userId").value(1L))
            .andExpect(jsonPath("[0].type").value("CHARGE"))
            .andExpect(jsonPath("[0].amount").value(2000L))
            .andExpect(jsonPath("[0].timeMillis").value(123L))
            .andExpect(jsonPath("[1].id").value(2L))
            .andExpect(jsonPath("[1].userId").value(1L))
            .andExpect(jsonPath("[1].type").value("USE"))
            .andExpect(jsonPath("[1].amount").value(1000L))
            .andExpect(jsonPath("[1].timeMillis").value(456L))
    }

    // 포인트 충전 API 단위 테스트
    @Test
    fun charge() {
        // given
        given(pointService.charge(1L, 1000L)).willReturn(UserPoint(1L, 2000L, 123L))

        // when then
        mockMvc.perform(
            patch("/point/{id}/charge", 1L)
                .content("1000")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("id").value(1L))
            .andExpect(jsonPath("point").value(2000L))
            .andExpect(jsonPath("updateMillis").value(123L))
    }

    // 포인트 사용 API 단위 테스트
    @Test
    fun use() {
        // given
        given(pointService.use(1L, 1000L)).willReturn(UserPoint(1L, 2000L, 123L))

        // when then
        mockMvc.perform(
            patch("/point/{id}/use", 1L)
                .content("1000")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("id").value(1L))
            .andExpect(jsonPath("point").value(2000L))
            .andExpect(jsonPath("updateMillis").value(123L))
    }
}