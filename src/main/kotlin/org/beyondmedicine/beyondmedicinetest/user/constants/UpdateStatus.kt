package org.beyondmedicine.beyondmedicinetest.user.constants

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "앱 업데이트 필요 상태")
enum class UpdateStatus {
    // 업데이트 필요 없음, 업데이트 필요, 강제 업데이트 필요
    @Schema(description = "업데이트 필요 없음")
    NO_UPDATE_REQUIRED, 
    
    @Schema(description = "업데이트 권장 (최신 버전이 있지만 강제는 아님)")
    UPDATE_REQUIRED, 
    
    @Schema(description = "강제 업데이트 필요 (최소 실행 가능 버전보다 낮음)")
    FORCE_UPDATE_REQUIRED
}