# Spring Batch Project Setup

## Overview

This project demonstrates a basic Spring Batch application for processing batch jobs. Spring Batch is a lightweight, comprehensive framework designed for processing large volumes of data in a robust, scalable, and transactional manner. It is ideal for tasks such as data migration, ETL (Extract, Transform, Load) processes, and report generation.

This guide explains how to set up and run the project locally using **Docker Compose** to provide a MySQL database for storing Spring Batch metadata.

## Prerequisites

To run this project locally, ensure you have the following installed:
- **Java 21** or later (JDK)
- **Maven** (for building the project)
- **Docker** and **Docker Compose** (for running the MySQL database)
- A code editor like IntelliJ IDEA, Eclipse, or VS Code (optional, for development)

## Project Structure

The project includes:
- A Spring Batch configuration with a simple job and step.
- A MySQL database (via Docker Compose) for storing Spring Batch metadata.
- A Maven build configuration (`pom.xml`) for managing dependencies.

## Setup Instructions

### Step 1: Clone the Repository

Clone this repository to your local machine:

```bash
git clone <repository-url>
cd <repository-directory>
```

### Step 2: Configure Docker Compose

A `docker-compose.yml` file is provided to set up a MySQL database. The file is configured to run MySQL with the necessary settings for Spring Batch.

1. Change a `docker-compose.yml` file in the root project with your preference `servers/docker-compose.yml`:

```yaml
version: '3.8'
services:
  mysqlData:
    image: mysql:latest
    ports:
      - "3306:3306"
    environment:
      - MYSQL_USER=batch_user
      - MYSQL_PASSWORD=user_batch_password
      - MYSQL_ROOT_PASSWORD=root_batch_password
    restart: always
    networks:
      - batch-network
    volumes:
      - ./sql.sql:/docker-entrypoint-initdb.d/sql.sql

networks:
  batch-network:
    driver: bridge
```

2. Start the MySQL database using Docker Compose:

```bash
docker-compose up -d
```

This command starts the MySQL container in the background. Verify that the container is running:

```bash
docker ps
```

You should see a container named `<repository-directory>_mysqlData_1` running on port `3306`.

### Step 3: Configure the Spring Batch Application

The Spring Batch application is configured to connect to the MySQL database. Ensure the `application.properties` or `application.yml` file in `src/main/resources` is set up as follows:

```properties
spring.batch.jdbc.initialize-schema=always
spring.batch.job.enabled=true

data01.datasource.jdbcUrl=jdbc:mysql://localhost:3306/data01
data01.datasource.username=batch_user
data01.datasource.password=batch_password
data01.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

data02.datasource.jdbcUrl=jdbc:mysql://localhost:3306/data01
data02.datasource.username=batch_user
data02.datasource.password=batch_password
data02.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```
```properties
spring:
    application:
        name: "spring_batch"

    batch:
        jdbc:
            initialize-schema: always

data01:
    datasource:
        driverClassName: com.mysql.cj.jdbc.Driver
        jdbcUrl: jdbc:mysql://localhost:3306/data01
        username: batch_user
        password: batch_password

data02:
    datasource:
        driverClassName: com.mysql.cj.jdbc.Driver
        jdbcUrl: jdbc:mysql://localhost:3306/data02
        username: batch_user
        password: batch_password

```

### Step 4: Build the Project

Build the project using Maven to download dependencies and compile the code:

```bash
mvn clean install
```

### Step 5: Run the Application

Run the Spring Boot application to execute the Spring Batch job:

```bash
mvn spring-boot:run
```

### Step 6: Verify the Output

- Check the MySQL database to verify that Spring Batch metadata tables (e.g., `BATCH_JOB_INSTANCE`, `BATCH_JOB_EXECUTION`) have been created and populated:

```bash
docker exec -it <container-name // container-id> mysql -U batch_db -p
```

Run the following SQL query:

```sql
SHOW TABLES;
```

You should see tables like `BATCH_JOB_INSTANCE`, `BATCH_JOB_EXECUTION`, etc.

### Step 7: Stop the Database

When you're done, stop and remove the Docker Compose services:

```bash
docker-compose down
```

This command stops the MySQL container and removes the associated network.

## Troubleshooting

- **Error: "Could not find bean of type 'JobBuilder'"**  
  Ensure you're using Spring Batch 5 and creating `JobBuilder` and `StepBuilder` instances manually, as they are no longer beans in the Spring context. Refer to the `BatchConfig` class example below:

```java
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Autowired
    public BatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job myJob() {
        return new JobBuilder("myJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(myStep())
                .build();
    }

    @Bean
    public Step myStep() {
        return new StepBuilder("myStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Executing step!");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
```

- **Database Connection Issues**  
  Verify that the MySQL container is running (`docker ps`) and that the `application.properties` or `application.yml` file has the correct database URL, username, and password.

- **Missing Dependencies**  
  Run `mvn clean install` to ensure all dependencies are downloaded correctly.

## Additional Resources

- [Spring Batch Official Documentation](https://docs.spring.io/spring-batch/docs/5.0.x/reference/html/)
- [Spring Batch 5.0 Migration Guide](https://docs.spring.io/spring-batch/docs/5.0.x/reference/html/whatsnew.html)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
