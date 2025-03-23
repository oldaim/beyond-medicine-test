package org.beyondmedicine.beyondmedicinetest.config

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import javax.sql.DataSource

@Configuration
class DataLoader {

    @Bean
    @Profile("!test") // 테스트 환경이 아닐 때만 실행
    fun testDataLoader(dataSource: DataSource): CommandLineRunner {
        return CommandLineRunner {
            val resource = ClassPathResource("data/sample-data.sql")
            val databasePopulator = ResourceDatabasePopulator(resource)
            databasePopulator.execute(dataSource)
            println("샘플 데이터가 성공적으로 로드되었습니다.")
        }
    }
}
