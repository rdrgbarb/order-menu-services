# ---- build stage ----
FROM public.ecr.aws/docker/library/maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
LABEL authors="rodrigo"

# Copy entire repo (simple + reliable for multi-module)
COPY . .

# Build only the menu module (and required parents)
RUN mvn -q -pl services/menu -am -DskipTests package

# ---- runtime stage ----
FROM public.ecr.aws/amazoncorretto/amazoncorretto:21-al2023-headless
WORKDIR /app

# Copy the built jar (exclude *.jar.original)
COPY --from=build /workspace/services/menu/target/*-SNAPSHOT.jar /app/app.jar

EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]
