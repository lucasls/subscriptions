run:
	docker-compose up -d
	./gradlew bootRun

restore:
	docker-compose down
	make run