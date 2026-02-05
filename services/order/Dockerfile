# ---- build stage ----
FROM public.ecr.aws/docker/library/maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
LABEL authors="rodrigo"

COPY . .

RUN mvn -q -pl services/order -am -DskipTests package

# ---- runtime stage ----
FROM public.ecr.aws/amazoncorretto/amazoncorretto:21-al2023-headless
WORKDIR /app

COPY --from=build /workspace/services/order/target/*-SNAPSHOT.jar /app/app.jar

EXPOSE 8082
ENTRYPOINT ["java","-jar","/app/app.jar"]
