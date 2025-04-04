# Etapa de build
FROM eclipse-temurin:21-jdk AS build

RUN apt-get update && apt-get install -y maven && apt-get clean

WORKDIR /app
COPY pom.xml ./
COPY src ./src

RUN mvn clean package

# Etapa de runtime corrigida (sem alpine!)
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/gerenciamento-financeiro-0.0.1-SNAPSHOT.jar gerenciamento-financeiro.jar
ENTRYPOINT ["java", "-jar", "gerenciamento-financeiro.jar"]
