# Weather API

A RESTful Weather API built with Java Spring Boot that fetches weather data from Visual Crossing's Weather API with Redis caching and rate limiting.

## Features

- ✅ Fetch real-time weather data from Visual Crossing API
- ✅ Redis caching with configurable TTL (default: 12 hours)
- ✅ Rate limiting to prevent API abuse (default: 10 requests per minute)
- ✅ Environment variable configuration
- ✅ Comprehensive error handling
- ✅ RESTful API endpoints
- ✅ Structured logging

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Redis server (running locally or remote)
- Visual Crossing API key (free tier available)

## Project Structure

```
Weather-API/
├── src/
│   └── main/
│       ├── java/com/weatherapi/
│       │   ├── WeatherApiApplication.java      # Main application class
│       │   ├── config/
│       │   │   └── RedisConfig.java            # Redis configuration
│       │   ├── controller/
│       │   │   └── WeatherController.java      # REST endpoints
│       │   ├── service/
│       │   │   └── WeatherService.java         # Business logic & caching
│       │   ├── model/
│       │   │   ├── WeatherResponse.java        # Main response model
│       │   │   ├── DayWeather.java             # Daily weather data
│       │   │   ├── HourWeather.java            # Hourly weather data
│       │   │   ├── CurrentConditions.java      # Current conditions
│       │   │   └── ErrorResponse.java          # Error response model
│       │   ├── filter/
│       │   │   └── RateLimitFilter.java        # Rate limiting filter
│       │   └── exception/
│       │       ├── GlobalExceptionHandler.java  # Global error handler
│       │       ├── WeatherApiException.java     # Custom exceptions
│       │       └── RateLimitExceededException.java
│       └── resources/
│           └── application.properties          # Application configuration
├── pom.xml                                     # Maven dependencies
├── .env.example                                # Environment variables template
└── README.md                                   # This file
```

## Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository-url>
cd Weather-API
```

### 2. Get Your API Key

1. Sign up for a free account at [Visual Crossing Weather API](https://www.visualcrossing.com/weather-api)
2. Copy your API key from the dashboard

### 3. Install and Start Redis

**On macOS:**
```bash
brew install redis
brew services start redis
```

**On Ubuntu/Debian:**
```bash
sudo apt-get install redis-server
sudo systemctl start redis-server
```

**Using Docker:**
```bash
docker run -d -p 6379:6379 --name redis redis:latest
```

### 4. Configure Environment Variables

Create a `.env` file in the project root:

```bash
cp .env.example .env
```

Edit `.env` and add your configuration:

```properties
WEATHER_API_KEY=your_actual_api_key_here
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=
CACHE_TTL_SECONDS=43200
RATE_LIMIT_CAPACITY=10
RATE_LIMIT_REFILL_TOKENS=10
RATE_LIMIT_REFILL_DURATION=60
```

### 5. Build the Project

```bash
mvn clean install
```

### 6. Run the Application

```bash
mvn spring-boot:run
```

The API will start on `http://localhost:8080`

## API Endpoints

### Get Weather by Query Parameter

```http
GET /api/weather?city={city}
```

**Example:**
```bash
curl "http://localhost:8080/api/weather?city=London"
```

### Get Weather by Path Parameter

```http
GET /api/weather/{city}
```

**Example:**
```bash
curl "http://localhost:8080/api/weather/New%20York"
```

### Clear Cache for Specific City

```http
DELETE /api/cache?city={city}
```

**Example:**
```bash
curl -X DELETE "http://localhost:8080/api/cache?city=London"
```

### Clear All Cache

```http
DELETE /api/cache/all
```

**Example:**
```bash
curl -X DELETE "http://localhost:8080/api/cache/all"
```

### Health Check

```http
GET /api/health
```

**Example:**
```bash
curl "http://localhost:8080/api/health"
```

## Response Format

### Successful Response

```json
{
  "resolvedAddress": "London, England, United Kingdom",
  "address": "London",
  "timezone": "Europe/London",
  "latitude": 51.5074,
  "longitude": -0.1278,
  "currentConditions": {
    "datetime": "14:30:00",
    "temp": 15.5,
    "feelsLike": 14.2,
    "humidity": 72,
    "windspeed": 12.5,
    "conditions": "Partly cloudy",
    "icon": "partly-cloudy-day"
  },
  "days": [
    {
      "datetime": "2025-12-27",
      "tempMax": 18.0,
      "tempMin": 12.0,
      "temp": 15.0,
      "humidity": 70,
      "conditions": "Partly cloudy",
      "description": "Partly cloudy throughout the day."
    }
  ],
  "source": "api",
  "cachedAt": 1703686200000
}
```

### Error Response

```json
{
  "status": 404,
  "message": "City not found: InvalidCity",
  "error": "WEATHER_API_ERROR",
  "timestamp": 1703686200000
}
```

### Rate Limit Response

```json
{
  "status": 429,
  "message": "Too many requests. Please try again later.",
  "error": "RATE_LIMIT_EXCEEDED",
  "timestamp": 1703686200000
}
```

## Configuration

### Cache Configuration

The default cache TTL is 12 hours (43200 seconds). You can modify this in `.env`:

```properties
CACHE_TTL_SECONDS=21600  # 6 hours
```

### Rate Limiting

Default: 10 requests per minute per IP address. Configure in `.env`:

```properties
RATE_LIMIT_CAPACITY=20              # Max tokens in bucket
RATE_LIMIT_REFILL_TOKENS=20         # Tokens to refill
RATE_LIMIT_REFILL_DURATION=60       # Refill duration in seconds
```

### Redis Configuration

```properties
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=your_redis_password  # If authentication is enabled
```

## Error Handling

The API handles the following errors gracefully:

- **400 Bad Request**: Invalid or missing parameters
- **401 Unauthorized**: Invalid API key
- **404 Not Found**: City not found
- **429 Too Many Requests**: Rate limit exceeded
- **500 Internal Server Error**: Server errors
- **503 Service Unavailable**: External API down

## Testing with cURL

```bash
# Get weather for London
curl "http://localhost:8080/api/weather?city=London"

# Get weather for New York
curl "http://localhost:8080/api/weather/New%20York"

# Get weather for Tokyo
curl "http://localhost:8080/api/weather?city=Tokyo"

# Clear cache for London
curl -X DELETE "http://localhost:8080/api/cache?city=London"

# Health check
curl "http://localhost:8080/api/health"
```

## Development

### Run in Development Mode

```bash
mvn spring-boot:run
```

### Build JAR

```bash
mvn clean package
java -jar target/weather-api-1.0.0.jar
```

### View Logs

Logs are configured in `application.properties`. Check console output for:
- Cache hits/misses
- API calls
- Rate limiting
- Error messages

## Architecture

### Caching Strategy

1. Client requests weather for a city
2. Check Redis cache using key: `weather:{city_name}`
3. If cache hit: return cached data (with `source: "cache"`)
4. If cache miss: fetch from Visual Crossing API
5. Store in Redis with TTL
6. Return fresh data (with `source: "api"`)

### Rate Limiting

- Uses Bucket4j library with token bucket algorithm
- Per-IP address limiting
- Configurable capacity and refill rate
- Returns HTTP 429 when exceeded

### Error Handling

- Global exception handler for all controllers
- Custom exceptions with appropriate HTTP status codes
- Detailed error messages in JSON format
- Logging for debugging

## Dependencies

- **Spring Boot 3.2.1**: Framework
- **Spring Data Redis**: Redis integration
- **Bucket4j**: Rate limiting
- **Lombok**: Reduce boilerplate code
- **Jackson**: JSON processing
- **Dotenv Java**: Environment variable management

## License

This project is open source and available under the MIT License.

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## Support

For issues and questions:
- Create an issue in the GitHub repository
- Check Visual Crossing API documentation: https://www.visualcrossing.com/resources/documentation/weather-api/
- Check Redis documentation: https://redis.io/docs/

## Acknowledgments

- Weather data provided by [Visual Crossing Weather API](https://www.visualcrossing.com/)
- Caching powered by [Redis](https://redis.io/)
- Rate limiting by [Bucket4j](https://github.com/bucket4j/bucket4j)
