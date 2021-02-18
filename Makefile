run:
	docker-compose up -d
	SPRING_PROFILES_ACTIVE=demo ./gradlew bootRun