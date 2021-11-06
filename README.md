Exchange Rate
----
Exchange Rate Service is used currency conversion.
Endpoints:
- GET /v1/currencyExchange/USD?amount=100&date=2021-03-03&toCurrency=AUD – used for converting the currency.
- GET /statistics – returns the statistic such as supported currencies and number of request for a currency.
- GET /public-link/USD?toCurrency=INR – returns public url for displaying chart for currency pairs.


#### Assumptions
- EUR will be used as the base currency for conversion.
- There will be api gateway for the throttling of request.
- Authentication/Authorization can also be implemented in the future.
- We can use lombok to get rid of getter/setters/hashcode/equals etc methods.

#### Technology Used
- Java
- SpringBoot
- Gradle
- Redis

#### Approach
We are hitting the ECB api for getting the currencyExchange reference data. ECB API response will be cached.
- We are using Redis cache, will be useful in distributed env.
- Single GET endpoint is managing conversion for the historical rates and current rates.
- Two separate caches namely 
   - `ecbExchangeRate` for daily rates
   - `ecbHistoricalExchangeRate` for historic rates
- Calculation of Statistics is done on the basis of interaction
- Public Url to view the chart of the currency pairs.

#### Steps to Run
- Build Steps
    - `./gradlew clean build`  for build
    - `./gradlew clean test` for unit test case
- Run Steps
    - `./gradlew bootRun` for running the application
- Entry Point
    - `com.scalable.capital.currencyExchange.rate.ApplicationLauncher` is the entry point of the application
    - Swagger Endpoint `http://localhost:8080/swagger-ui/index.html`

#### Steps to run docker
- Build Steps
  - `./gradlew clean build`  for build
- Docker Commands
  - `docker-compose build`  
  - `docker-compose up`  