#
# 
# needed jdk on ubuntu ( if you want to check whether jdk is installed, type "javac -version" in CLI. )
# $ sudo apt install openjdk-17-jdk
# 

# specific base image
# FROM eclipse-temurin:17.0.7_7-jre-alpine
FROM eclipse-temurin:17.0.7_7-jre-alpine 

# copy into container
COPY ./build/libs/NetIncomeSimulator-0.0.1-SNAPSHOT.jar /NetIncomeSimulator-0.0.1-SNAPSHOT.jar
# docker volume create devspring

# app port
EXPOSE 8080
# default command when containers start
CMD ["java","-jar","/NetIncomeSimulator-0.0.1-SNAPSHOT.jar"]
